package com.monteBravo.be.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mercadopago.resources.payment.Payment;
import com.monteBravo.be.DTO.emailDto.EmailDTO;
import com.monteBravo.be.DTO.emailDto.OrderDTO;
import com.monteBravo.be.DTO.emailDto.OrderItemDTO;
import com.monteBravo.be.DTO.emailDto.TemplateDTO;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.EmailNotificationRepository;
import com.monteBravo.be.Services.SendEmail;
import com.monteBravo.be.Services.TemplateMailService;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.EmailNotification;
import com.monteBravo.be.entity.TemplateMail;
import com.sendgrid.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("api/v1/email")
public class EmailController {

    @Autowired
    private SendEmail emailService;
    private HttpServletRequest request;
    @Autowired
    private ClienteRepository clientesRepository;
    @Autowired
    private EmailNotificationRepository emailNotificationRepository;
    @Autowired
    private TemplateMailService templateMailService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/send-email-batch")
    @Operation(summary = "Enviar uno o muchos emails a través de SendGrid un provedor de servicio de envio", description = "Permite enviar emails a los clientes proveyendo información relevante, utilizando plantillas de correo configuradas previamente en la aplicacion de SendGrid. Estas pueden contener variables que deben ser provistas por el sistema o el usuario. Si la operación es exitosa, el correo se envía correctamente. ")
    public ResponseEntity<?> sendEmailBatch(@RequestBody  EmailDTO datosEmail){
        List<Map<String, String>> camposAdicionales = datosEmail.getCamposAdicionales();

        // chekeo en api tambien que no halla campos vacios
        boolean hasEmptyFields = camposAdicionales.stream()
                .anyMatch(map -> map.values().stream().anyMatch(value -> value == null || value.trim().isEmpty()));

        if (hasEmptyFields) {
            return ResponseEntity.badRequest().body("Hay campos vacíos en los campos adicionales.");
        }

        List<Cliente> clientes =  clientesRepository.findByIds(datosEmail.getIdClientes());
        if(clientes.isEmpty()){

            return ResponseEntity.status(400).body("No hay destinatarios de este email");
        }
       try{
           Response r =  emailService.sendEmailBatch(clientes, datosEmail.getCamposAdicionales(), datosEmail.getIdTemplate());
            return ResponseEntity.status(r.getStatusCode()).body(r.getBody());
       }catch(Exception e ){
          System.out.println("error "+e);
           return ResponseEntity.status(500).body("Error de sistema");
       }

    }

    @PostMapping("/send-email-mkt")
    public ResponseEntity<?> sendEmailMkt(@RequestBody EmailDTO emailRequest) {
        String templateId = emailRequest.getIdTemplate();
        List<Long> clientIds = emailRequest.getIdClientes();

        Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
        if (tm.isEmpty()) {
            return ResponseEntity.status(404).body("No encontramos un Template con el id: " + templateId + " Verifique en SendGrid");
        }

        List<Cliente> clientes = clientesRepository.findByIds(clientIds);
        if (clientes.isEmpty()) {
            return ResponseEntity.status(404).body("Debe seleccionar al menos un usuario para enviar el Email");
        }
        List<TemplateDTO> tv = templateMailService.getVariablesByTemplateId(templateId);
        if (tv.isEmpty()) {
            System.out.println("Está enviándose un mail sin ninguna variable parametrizada");
        }

        try {
            Response resp = emailService.sendEmailMarketing(tv, clientes, templateId);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (Exception e) {
            System.out.println("Error " + e);
            return ResponseEntity.status(500).body("Ocurrió un error al realizar el envío del mail");
        }
    }



    // sendgrid nos notifica el estado de el envio de email
    @PostMapping("/events")
    @Operation(summary = "Recive informacion relevante sobre el estado de envio de emails", description = "Guarda los estados de los emails enviados ")
    public ResponseEntity<Void> handleSendGridEvents(@RequestBody List<Map<String, Object>> events) {
        if (events == null || events.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Validación básica
        }

        for (Map<String, Object> event : events) {
            // Extraer datos principales del evento
            String messageId = (String) event.get("sg_message_id");
            String eventType = (String) event.get("event");
            EmailNotification notification = new EmailNotification();
            notification.setEmail((String) event.get("email"));
            notification.setEventType(eventType);
            notification.setIdSendGrid(messageId);

            // Convierte el timestamp
            Long timestamp = ((Number) event.get("timestamp")).longValue();
            notification.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()));

            // Guarda en la base de datos
            emailNotificationRepository.save(notification);

        }

        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/email_templates_sendgrid")
    @Operation(summary = "Obtiene todos los templates de email dinamico de SendGrid", description = "Solicita todos los templates dinamicos a Send grid y los envia al cliente")
    public ResponseEntity<?> getPlantilla()  {
        try{
            List<JsonNode> respuesta = emailService.getDynamicTemplates();
            return ResponseEntity.ok().body(respuesta);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }



  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping("/delete_template/{id}")
  @Operation(
          summary = "Eliminar un template dinamico",
          description = "Permite emilinar un template dinamico"
  )
  public ResponseEntity<?> deleteTemplate(@PathVariable("id")  String  id){

    try{
        ResponseEntity<?> r = emailService.deleteTemplate(id);

        return ResponseEntity.status(r.getStatusCode()).body(r.getBody());
    }catch(Exception e){
        System.out.println("error delete "+e.getMessage());
        return ResponseEntity.status(500).body("Error de sistema");
    }

  }




}
