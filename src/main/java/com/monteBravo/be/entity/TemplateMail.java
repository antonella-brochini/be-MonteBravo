package com.monteBravo.be.entity;

import com.monteBravo.be.entity.enums.EmailType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name= "templateMail")
public class TemplateMail {
    @Id
    private String id;

    private String name;

    private String asunto;

    private String description;

    @Enumerated(EnumType.STRING) // Almacena el valor como texto en la base de datos
    private EmailType tipoEmail;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateVariable> variables;

    public TemplateMail(){};

    public TemplateMail(String name, String description, List<TemplateVariable> variables, String id, String asunto) {
        this.name = name;
        this.description = description;
        this.variables = variables;
        this.id = id;
        this.asunto = asunto;
    }
}
