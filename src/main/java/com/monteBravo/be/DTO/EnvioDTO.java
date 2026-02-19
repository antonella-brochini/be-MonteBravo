package com.monteBravo.be.DTO;

import jakarta.validation.constraints.NotNull;

public class EnvioDTO {
    private String sucursal;
    private String streetName;
    private String streetNumber;
    private String aclaracion;
    private String city;
    private String state;
    private String postalCode;
    private String metodoEnvio; // Por ejemplo: "Estándar", "Express"
    private Double costoEnvio;
    @NotNull(message = "El email no puede ser nulo")
    private String email;
    @NotNull(message = "El email no puede ser nulo")
   private String celular;

    // Getters y Setters

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public String getMetodoEnvio() {
        return metodoEnvio;
    }

    public void setMetodoEnvio(String metodoEnvio) {
        this.metodoEnvio = metodoEnvio;
    }

    public Double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(Double costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getState() {
        return state;
    }

    public void setState(String departamento) {
        this.state = departamento;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String retiraEnSucursal) {
        this.sucursal = retiraEnSucursal;
    }


    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getAclaracion() {
        return aclaracion;
    }

    public void setAclaracion(String aclaracion) {
        this.aclaracion = aclaracion;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public EnvioDTO(String sucursal, String celular, String email, Double costoEnvio, String metodoEnvio, String postalCode, String state, String city, String aclaracion, String streetNumber, String streetName) {
        this.sucursal = sucursal;
        this.celular = celular;
        this.email = email;
        this.costoEnvio = costoEnvio;
        this.metodoEnvio = metodoEnvio;
        this.postalCode = postalCode;
        this.state = state;
        this.city = city;
        this.aclaracion = aclaracion;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
    }

    public EnvioDTO() {}
}
