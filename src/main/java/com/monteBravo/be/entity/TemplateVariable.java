package com.monteBravo.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "template_variables")
public class TemplateVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    @JsonIgnore
    private TemplateMail template;

    private String variable;
    @Column(nullable = true)
    private String nombrePropiedad;

    public TemplateVariable(){};

    public TemplateVariable(TemplateMail template, String variable, String nombrePropiedad) {
        this.template = template;
        this.variable = variable;
        this.nombrePropiedad  = nombrePropiedad;
    }
}
