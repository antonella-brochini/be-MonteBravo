package com.monteBravo.be.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "opinion_productos" )
public class OpinionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long IdOpinionProducto;
    private String opinion;
    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
    @ManyToOne
    @JoinColumn(name = "idUser")
    private Cliente cliente;
    private double estrellas; // numero entre 0 a 5 con posibilidad de ser 1.5 2.5 etc

    public OpinionProducto(String opinion, Producto producto, Cliente cliente, double estrellas) {
        this.opinion = opinion;
        this.producto = producto;
        this.cliente = cliente;
        this.estrellas = estrellas;
    }
    public OpinionProducto() {}

    public double getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(double estrellas) {
        this.estrellas = estrellas;
    }

    public long getIdOpinionProducto() {
        return IdOpinionProducto;
    }

    public void setIdOpinionProducto(long idOpinionProducto) {
        IdOpinionProducto = idOpinionProducto;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "OpinionProducto{" +
                "IdOpinionProducto=" + IdOpinionProducto +
                ", opinion='" + opinion + '\'' +
                ", producto=" + producto +
                ", cliente=" + cliente +
                '}';
    }
}
