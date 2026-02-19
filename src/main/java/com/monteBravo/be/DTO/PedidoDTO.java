package com.monteBravo.be.DTO;

import java.util.Date;

public class PedidoDTO {
    private long idPedido;
    private Date fechaCompra;
    private double total;

    public PedidoDTO(long idPedido, Date fechaCompra, double total) {
        this.idPedido = idPedido;
        this.fechaCompra = fechaCompra;
        this.total = total;
    }
    public PedidoDTO(){}

    public long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(long idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
