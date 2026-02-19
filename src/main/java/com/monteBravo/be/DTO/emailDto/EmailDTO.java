package com.monteBravo.be.DTO.emailDto;

import java.util.List;
import java.util.Map;

public class EmailDTO {

    private List<Long> idClientes;

    private String idTemplate;

    private List<Map<String, String>> camposAdicionales;

    public List<Long> getIdClientes() {
        return idClientes;
    }

    public void setIdClientes(List<Long> idClientes) {
        this.idClientes = idClientes;
    }

    public List<Map<String, String>> getCamposAdicionales() {
        return camposAdicionales;
    }

    public void setCamposAdicionales(List<Map<String, String>> camposAdicionales) {
        this.camposAdicionales = camposAdicionales;
    }

    public String getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(String idTemplate) {
        this.idTemplate = idTemplate;
    }
/*
    public EmailDTO(List<Long> idClientes,  List<Map<String, String>> variables, String idTemplate) {
        this.idClientes = idClientes;
        this.camposAdicionales = variables;
        this.idTemplate = idTemplate;
    }*/

    public EmailDTO(List<Long> idClientes,String idTemplate){
        this.idClientes = idClientes;
        this.idTemplate = idTemplate;
    }



}
