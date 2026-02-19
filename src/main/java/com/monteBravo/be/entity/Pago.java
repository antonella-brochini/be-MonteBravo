package com.monteBravo.be.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "id_mercado_pago")
    private Long idMercadoPago;
    private String estado;
    private String statusDetail;
    private LocalDateTime ultimaActualizacion;

    public Pago() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMercadoPago() {
        return idMercadoPago;
    }

    public void setIdMercadoPago(Long id) {
        this.idMercadoPago = id;
    }



    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }



    public Pago(String estado, Long id) {
        this.estado = estado;
        idMercadoPago = id;
    }


}
