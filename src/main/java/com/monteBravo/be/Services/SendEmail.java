package com.monteBravo.be.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import com.monteBravo.be.DTO.emailDto.ResetPassDTO;
import com.monteBravo.be.DTO.emailDto.NotificacacionAdmiDTO;
import com.monteBravo.be.DTO.emailDto.OrderDTO;
import com.monteBravo.be.DTO.emailDto.OrderItemDTO;
import com.monteBravo.be.DTO.emailDto.TemplateDTO;
import com.monteBravo.be.Inteface.IResolvedorVariable;
import com.monteBravo.be.Repository.AdministradorRepository;
import com.monteBravo.be.entity.*;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.io.IOException;

@Service
public class SendEmail {

    private static final String MAIL = "montebravo.uy@gmail.com";
    private static final String NAME_MAIL = "Monte Bravo";
    private static final String DELETE_SENDGRID = "https://api.sendgrid.com/v3/templates/";
    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/templates?generations=dynamic";
    @Value("${spring.sendgrid.api-key}")
    private String sendGridApiKey;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TemplateMailService templateMailService;
    @Autowired
    private AdministradorRepository admiRepo;
   /*ESTE METODO HAY QUE ELIMINARLO YA QUE SE RESUELVE DE FORMA GENERICA EN sendEmailMkt*/
    public Response sendEmailBatch(List<Cliente> clientes, List<Map<String, String>> variables, String templateId) throws IOException {
        Mail mail = resolverDatosGenericos(templateId);

// Añadir destinatarios
        // variable, content
        // variable que puso el usuario en el template, contenido que desea asociar a esa variable

        // Obtenemos el getter dinámico de cliente basado en el string content

        for (Cliente cliente : clientes) {
            Personalization personalization = new Personalization();
            Email to = new Email(cliente.getEmail());
            personalization.addTo(to);
            variables.forEach(map -> {
                // Agrega las variables dinámicas específicas del cliente
               String contentValue = map.get("content");
               String variableValue = map.get("variable");
                    Function<Cliente, String> getter = Cliente.AtributoHandler.checkear(contentValue);
                    if(getter!=null){
                        String  valor = getter.apply(cliente);
                        personalization.addDynamicTemplateData(variableValue, valor);
                    }else{
                        personalization.addDynamicTemplateData(variableValue, contentValue);
                    }

            });
            // Agrega la personalización al correo
            mail.addPersonalization(personalization);
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Status Code: " + response.getStatusCode());
            return response;

        } catch (IOException ex) {
            throw ex;
        }
    }

    public List<JsonNode> getDynamicTemplates() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + sendGridApiKey);
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =  restTemplate.exchange(SENDGRID_URL, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode templatesNode = rootNode.get("templates");

            if (templatesNode != null && templatesNode.isArray()) {
                List<JsonNode> templates = new ArrayList<>();
                templatesNode.forEach(templates::add);
                return templates;
            }

            return null;
        } catch (HttpClientErrorException | JsonProcessingException e) {
            return null;
        }
    }

    public  ResponseEntity<?>  deleteTemplate(String  templateId)  throws IOException {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + sendGridApiKey);
            headers.set("Content-Type", "application/json");
            HttpEntity<Object> entity = new HttpEntity<>(headers);
            ResponseEntity<?> response =  restTemplate.exchange(DELETE_SENDGRID+templateId, HttpMethod.DELETE, entity, String.class);
            return response;


    }


    // Reestablecer contrasena
    public void sendPasswordResetEmail(Usuario user, String url){
    String templateId = "d-73426a9b8d8d462c8f24fdc42368b87d";
    ResetPassDTO resPass = new ResetPassDTO(user.getNombreUsuario() , url );

    Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
    if (tm.isEmpty()) {
        System.out.println("Está enviándose un mail sin template id correcto");
    }
    List<TemplateDTO> tv = templateMailService.getVariablesByTemplateId(templateId);
    if (tv.isEmpty()) {
        System.out.println("Está enviándose un mail sin ninguna variable parametrizada");
    }

    try {
        Mail mail = resolverDatosGenericos(templateId);
        Personalization personalization = new Personalization();
        Email to = new Email(user.getEmail());
        personalization.addTo(to);
        resolverVariable(personalization, resPass, tv);
        mail.addPersonalization(personalization);
        try {
            getResponse(mail);

        }catch(IOException io){
            System.out.println(io.getMessage());
        }

    } catch (Exception e) {
        System.out.println("Error " + e);
    }
}


    /*Este metodo se deberia utilizar para los Email de marketing, donde por lo general suelen solo tener el Nombre del usuario
    * y el resto de informacion es generica...*/
    public Response sendEmailMarketing(List<TemplateDTO> tv, List<Cliente> clientes, String templateId) {
        Mail mail = resolverDatosGenericos(templateId);
        for(Cliente c : clientes){
            Personalization personalization = new Personalization();
            Email to = new Email(c.getEmail());
            personalization.addTo(to);
            resolverVariable(personalization, c, tv);
            mail.addPersonalization(personalization);
        }
        try {
            return getResponse(mail);
        }catch(IOException io){
            System.out.println(io.getMessage());
        }
        return null;
    }

// notificar admin sobreventa de productos

    public void notificarAdminSobreventa(Payment payment, List<MovimientoStock> lista){
        String templateId = "d-96804d0457e14d00a12f23c2304c9e5f";
        Map<String, Object> metadata = payment.getMetadata();

        NotificacacionAdmiDTO not = new NotificacacionAdmiDTO();
        not.setIdMercadoPago(payment.getId().toString());

        Object phoneObj = metadata.get("phone");
        not.setCelularCliente(phoneObj.toString());
        Object emailObj = metadata.get("email");
        not.setEmailCliente(emailObj.toString());
        not.setEstado(payment.getStatus());
        not.setDetalleEstado(payment.getStatusDetail());

        List<OrderItemDTO> listaMov = new ArrayList<>();
        for(MovimientoStock m : lista){
            OrderItemDTO oi =  new OrderItemDTO();
            oi.setProductName(m.getProducto().getNombre());
            listaMov.add(oi);
        }
        not.setOrderItems(listaMov);
        Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
        if (tm.isEmpty()) {
            System.out.println("Está enviándose un mail sin template id correcto");
        }

        List<TemplateDTO> tv = templateMailService.getVariablesByTemplateId(templateId);
        if (tv.isEmpty()) {
            System.out.println("Está enviándose un mail sin ninguna variable parametrizada");
        }

        try {
            Response resp = sendEmailSobreventa(tv, not, templateId);
        } catch (Exception e) {
            System.out.println("Error " + e);
        }



    }

    public Response sendEmailSobreventa(List<TemplateDTO> tv, NotificacacionAdmiDTO not, String templateId){
        Mail mail = resolverDatosGenericos(templateId);
        //email admis
        List<Administrador> admis = admiRepo.findAllEnabled();
        Personalization personalization = new Personalization();
        for(Administrador a :admis ){
            Email to = new Email(a.getEmail());
            personalization.addTo(to);
        }
        resolverVariable(personalization, not, tv);
        List<Map<String, Object>> orderItemsData = new ArrayList<>();
        for (OrderItemDTO item : not.getOrderItems()) {
            Map<String, Object> itemData = new HashMap<>();
            resolverVariableToMap(item, tv, itemData);
            orderItemsData.add(itemData);
        }
        personalization.addDynamicTemplateData("orderItems", orderItemsData);
        mail.addPersonalization(personalization);
        try {
            return getResponse(mail);
        }catch(IOException io){
            System.out.println(io.getMessage());
        }
        return null;
    }

// notificar admin de devoluciones y contracargos
    public void notificarAdmin(Payment payment){
        String templateId = "d-48354e827fde47dbbb2e2477e4d8f17b";
        Map<String, Object> metadata = payment.getMetadata();

        NotificacacionAdmiDTO not = new NotificacacionAdmiDTO();
        not.setIdMercadoPago(payment.getId().toString());
        Object phoneObj = metadata.get("phone");
        not.setCelularCliente(phoneObj.toString());
        Object emailObj = metadata.get("email");
        not.setEmailCliente(emailObj.toString());
        not.setEstado(payment.getStatus());
        not.setDetalleEstado(payment.getStatusDetail());
        not.setIdMercadoPago(payment.getId().toString());

        Instant ahoraEnUruguay = Instant.now().atZone(ZoneId.of("America/Montevideo")).toInstant();
        Date fechaEnFormatoDate = Date.from(ahoraEnUruguay);
        not.setFecha(fechaEnFormatoDate);


        Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
        if (tm.isEmpty()) {
            System.out.println("Está enviándose un mail sin template id correcto");
        }

        List<TemplateDTO> tv = templateMailService.getVariablesByTemplateId(templateId);
        if (tv.isEmpty()) {
            System.out.println("Está enviándose un mail sin ninguna variable parametrizada");
        }

        try {
            Response resp = sendEmailNotAdmin(tv, not, templateId);
        } catch (Exception e) {
            System.out.println("Error " + e);
        }

    }

    public Response sendEmailNotAdmin(List<TemplateDTO> tv, NotificacacionAdmiDTO not, String templateId){
        Mail mail = resolverDatosGenericos(templateId);

        Personalization personalization = new Personalization();
        //email admis
        List<Administrador> admis = admiRepo.findAllEnabled();

        for(Administrador a :admis ){
            Email to = new Email(a.getEmail());
            personalization.addTo(to);
            resolverVariable(personalization, not, tv);
            mail.addPersonalization(personalization);
        }

        mail.addPersonalization(personalization);
        try {
            return getResponse(mail);
        }catch(IOException io){
            System.out.println(io.getMessage());
        }
        return null;
    }


// notificar cliente estado del pago aprovado o pendiente/en proceso
    public void notificarClienteEstadoPago(Payment payment){
        Map<String, Object> metadata = payment.getMetadata();
        Object emailObj = metadata.get("email");
        String templateId="";
        if(payment.getStatus().equals("approved")){
            templateId = "d-5a31e03116df484c8c493df2610bacb2";
        }else{
            templateId = "d-b3b31e324ad84b1b9ab519aee3babf56";
        }

        OrderDTO order = new OrderDTO();
        order.setOrderId(payment.getId().toString());

        Cliente c = new Cliente();
        c.setEmail( emailObj.toString());
        order.setCliente(c);

        Instant ahoraEnUruguay = Instant.now().atZone(ZoneId.of("America/Montevideo")).toInstant();
        Date fechaEnFormatoDate = Date.from(ahoraEnUruguay);
        order.setPaymentDate(fechaEnFormatoDate);

        //envio
        double costoEnvio = payment.getShippingAmount() != null ? payment.getShippingAmount().doubleValue() : 0;
        order.setCostoEnvio(costoEnvio);

        List<PaymentItem> itemsPayment = payment.getAdditionalInfo().getItems();
        Double totalAmount = 0.0;
        List<OrderItemDTO> orderItems = new ArrayList<>();
        for (PaymentItem itemPay : itemsPayment) {
            OrderItemDTO item = new OrderItemDTO();
            totalAmount+= (itemPay.getQuantity().doubleValue()*itemPay.getUnitPrice().doubleValue());
            item.setProductName(itemPay.getTitle());
            item.setQuantity(itemPay.getQuantity());
            item.setPrice(itemPay.getUnitPrice().doubleValue());
            item.setSubtotal(itemPay.getQuantity()*itemPay.getUnitPrice().doubleValue());
            orderItems.add(item);
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
        if (tm.isEmpty()) {
            System.out.println("Está enviándose un mail sin template id correcto");
        }

        List<TemplateDTO> tv = templateMailService.getVariablesByTemplateId(templateId);
        if (tv.isEmpty()) {
            System.out.println("Está enviándose un mail sin ninguna variable parametrizada");
        }

        try {
            Response resp = sendEmailConfPedido(tv, order, templateId);
        } catch (Exception e) {
            System.out.println("Error " + e);
        }

    }

    public Response sendEmailConfPedido(List<TemplateDTO> tv, OrderDTO order, String templateId){

        Mail mail = resolverDatosGenericos(templateId);

        Cliente c = order.getCliente();
        Personalization personalization = new Personalization();
        Email to = new Email(c.getEmail());
        personalization.addTo(to);
        resolverVariable(personalization, order, tv);
        List<Map<String, Object>> orderItemsData = new ArrayList<>();
        for (OrderItemDTO item : order.getOrderItems()) {
            Map<String, Object> itemData = new HashMap<>();
            resolverVariableToMap(item, tv, itemData);
            orderItemsData.add(itemData);
        }
        personalization.addDynamicTemplateData("orderItems", orderItemsData);
        mail.addPersonalization(personalization);
        try {
            return getResponse(mail);
        }catch(IOException io){
            System.out.println(io.getMessage());
        }
        return null;
    }

    // metodos auxiliares
    private Mail resolverDatosGenericos(String templateId){
        Email from = new Email(MAIL);
        from.setName(NAME_MAIL);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);
        return mail;
    }

    private void resolverVariable(Personalization p, IResolvedorVariable objResolvedor, List<TemplateDTO> tv) {
        for (TemplateDTO t : tv) {
            String nombreVariable = t.getVariableName();
            String valorVariable;
            if (t.getNombreProiedadVariable() != null && !t.getNombreProiedadVariable().isEmpty()) {
                valorVariable = t.getNombreProiedadVariable();
            } else {
                valorVariable = objResolvedor.devolverGetterPorNombre(nombreVariable);
            }
             p.addDynamicTemplateData(nombreVariable, valorVariable);

        }
    }

    private void resolverVariableToMap(IResolvedorVariable objResolvedor, List<TemplateDTO> tv, Map<String, Object> map) {
        for (TemplateDTO t : tv) {
            String nombreVariable = t.getVariableName();
            String valorVariable;

            if (t.getNombreProiedadVariable() != null) {
                valorVariable = t.getNombreProiedadVariable();
            } else {
                valorVariable = objResolvedor.devolverGetterPorNombre(nombreVariable);
            }

            map.put(nombreVariable, valorVariable != null ? valorVariable : "");
        }
    }

    private Response getResponse(Mail mail) throws IOException {
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Status Code: " + response.getStatusCode());
            return response;

        } catch (IOException ex) {
            throw ex;
        }
    }
}






