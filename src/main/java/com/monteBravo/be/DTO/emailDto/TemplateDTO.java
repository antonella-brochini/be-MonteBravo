package com.monteBravo.be.DTO.emailDto;

import com.monteBravo.be.entity.enums.EmailType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TemplateDTO {
    private String templateId;
    private String templateDescription;
    private String templateName;
    private Long variableId;
    private String variableName;
    private String nombreProiedadVariable;
    private String asunto;
    private EmailType tipoEmail;

    public TemplateDTO(String templateId, String templateDescription, String templateName, Long variableId, String variableName,
                       String asunto, String nombreProiedadVariable, EmailType tipoEmail) {
        this.templateId = templateId;
        this.templateDescription = templateDescription;
        this.templateName = templateName;
        this.variableId = variableId;
        this.variableName = variableName;
        this.nombreProiedadVariable = nombreProiedadVariable;
        this.asunto = asunto;
        this.tipoEmail = tipoEmail;
    }
}
