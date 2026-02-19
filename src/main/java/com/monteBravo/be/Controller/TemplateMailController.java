package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.emailDto.TemplateDTO;
import com.monteBravo.be.Services.TemplateMailService;
import com.monteBravo.be.entity.TemplateMail;
import com.monteBravo.be.entity.TemplateVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/template")
@Tag(name = "Template Management", description = "API para gestionar plantillas de correos y sus variables dinámicas")
public class TemplateMailController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateMailController.class);

    private final TemplateMailService templateMailService;

    public TemplateMailController(TemplateMailService templateMailService) {
        this.templateMailService = templateMailService;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las plantillas", description = "Devuelve una lista de todas las plantillas almacenadas.")
    public ResponseEntity<List<TemplateMail>> getAllTemplates() {
        List<TemplateMail> templates = templateMailService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una plantilla por ID", description = "Devuelve los detalles de una plantilla específica dado su ID.")
    public ResponseEntity<TemplateMail> getTemplateById(
            @Parameter(description = "ID de la plantilla", required = true)
            @PathVariable String id) {
        return templateMailService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Guardar o actualizar una plantilla", description = "Guarda una nueva plantilla o actualiza una existente.")
    public ResponseEntity<?> saveTemplate(@RequestBody TemplateMail templateMail) {
        Optional<TemplateMail> existe = templateMailService.getTemplateById(templateMail.getId());
        if(existe.isPresent()) {
            TemplateMail existente = existe.get();
            existente.setTipoEmail(templateMail.getTipoEmail());
            templateMailService.saveTemplate(existente);
            logger.info("El template ya existe y fue actualizado correctamente");
            return ResponseEntity.ok("El template ya existe y fue actualizado correctamente");

        }
        TemplateMail savedTemplate = templateMailService.saveTemplate(templateMail);
        return ResponseEntity.ok(savedTemplate);
    }
    @PostMapping("/variables/{templateId}")
    public ResponseEntity<?> saveVariablesTemplate(
            @PathVariable String templateId,
            @RequestBody List<TemplateVariable> templateVariables) {

        Optional<TemplateMail> tm = templateMailService.getTemplateById(templateId);
        if (tm.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TemplateMail templateMail = tm.get();
        templateVariables.forEach(variable -> variable.setTemplate(templateMail));

        List<TemplateVariable> savedVariables = templateMailService.saveAllVariables(templateVariables);

        return ResponseEntity.ok(savedVariables);
    }

    @GetMapping("/detalle/{templateId}")
    public ResponseEntity<List<TemplateDTO>> getVariablesByTemplateId(@PathVariable String templateId) {
        List<TemplateDTO> variables = templateMailService.getVariablesByTemplateId(templateId);
        return ResponseEntity.ok(variables);
    }


    @GetMapping("/detalle")
    public ResponseEntity<List<TemplateDTO>> getAllTemplateVariables() {
        List<TemplateDTO> variables = templateMailService.getAllTemplateVariables();
        return ResponseEntity.ok(variables);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una plantilla", description = "Elimina una plantilla específica dado su ID.")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "ID de la plantilla a eliminar", required = true)
            @PathVariable String id) {
        templateMailService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/variables/{id}")
    public ResponseEntity<?> eliminarVariables(@PathVariable String id,  @RequestBody List<Integer> idVariables ){
        try {
            Optional<TemplateMail> tm = templateMailService.getTemplateById(id);
            if(!tm.isPresent()){
                logger.info("No existe un template con ese id: " + id);
                return  ResponseEntity.status(404).body("No existe un template con ese id: " + id);
            }
            return templateMailService.deleteVariables(idVariables);
        }catch(Exception e){

        }
        return  ResponseEntity.noContent().build();
    }
}
