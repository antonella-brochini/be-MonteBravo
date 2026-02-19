package com.monteBravo.be.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemCarritoDTO {
    @NotNull()
    private Long idProducto;
    private String nombre;
    private float precio;
    @Min(1)
    private int cantidad;

    public ItemCarritoDTO(Long id, String titulo, float precio, int cantidad) {
        this.idProducto = id;
        this.nombre = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public @NotNull() Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(@NotNull() Long idProducto) {
        this.idProducto = idProducto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    @Min(1)
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(@Min(1) int cantidad) {
        this.cantidad = cantidad;
    }



}