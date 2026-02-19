package com.monteBravo.be.DTO;

public class OpinionProductoDTO {
    private String opinion;
    private double estrellas; // Entre 0 y 5
    private String usuario; // Nombre o email del usuario que dejó la opinión

    public OpinionProductoDTO(String opinion, double estrellas, String usuario) {
        this.opinion = opinion;
        this.estrellas = estrellas;
        this.usuario = usuario;
    }

    public OpinionProductoDTO(){}
    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public double getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(double estrellas) {
        this.estrellas = estrellas;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
