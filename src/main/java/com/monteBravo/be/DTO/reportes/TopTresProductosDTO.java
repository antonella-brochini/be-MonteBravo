package com.monteBravo.be.DTO.reportes;

public class TopTresProductosDTO {

    private String nombre;
    private long cantidad;

    public TopTresProductosDTO(Object[] row) {
        this.nombre = (String) row[0];;
        this.cantidad = ((Number) row[1]).intValue();;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }
}
