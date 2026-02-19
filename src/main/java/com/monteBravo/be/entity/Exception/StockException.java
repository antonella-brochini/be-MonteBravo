package com.monteBravo.be.entity.Exception;

import com.monteBravo.be.entity.Producto;

public class StockException extends Exception{
    private Producto producto;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public StockException(String message, Producto producto) {
        super(message);
        this.producto = producto;

    }
}
