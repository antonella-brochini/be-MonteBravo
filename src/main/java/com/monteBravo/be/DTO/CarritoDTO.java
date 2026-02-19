package com.monteBravo.be.DTO;

import com.monteBravo.be.entity.CarritoProducto;
import com.monteBravo.be.entity.Cliente;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class CarritoDTO {

    private long idCarrito;
    @NotNull
    private List<ItemCarritoDTO> productos;


    public long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public List<ItemCarritoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ItemCarritoDTO> productos) {
        this.productos = productos;
    }




    public CarritoDTO() {

    }

    public CarritoDTO(long idCarrito, List<ItemCarritoDTO> productos) {
        this.idCarrito = idCarrito;
        this.productos = productos;
    }

    @Override
    public String toString() {
        String s = "CarritoDTO{" +
                "idCarrito=" + idCarrito +
                ", productos=" + productos.getFirst().getIdProducto() +
                '}';
    return s;}
}
