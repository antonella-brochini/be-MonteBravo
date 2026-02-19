package com.monteBravo.be.DTO.reportes;

import java.util.Date;

public class ReporteCarritoAbandonadoDTO {
    private long idCarrito;
    private long idUsuario;
    private Date ultimaInteraccion;
    private String nombreUsuario;
    private String Email;
    private double totalCarrito;

    public ReporteCarritoAbandonadoDTO(Object[] row){
        this.idCarrito = ((Number) row[0]).longValue();
        this.idUsuario = ((Number) row[1]).longValue();
        this.ultimaInteraccion = (Date) row[2];
        this.nombreUsuario = row[3].toString();
        this.Email = row[4].toString();
        this.totalCarrito = ((Number) row[5]).doubleValue();
    }

    public double getTotalCarrito() {
        return totalCarrito;
    }

    public void setTotalCarrito(double totalCarrito) {
        this.totalCarrito = totalCarrito;
    }

    public long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getUltimaInteraccion() {
        return ultimaInteraccion;
    }

    public void setUltimaInteraccion(Date ultimaInteraccion) {
        this.ultimaInteraccion = ultimaInteraccion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
