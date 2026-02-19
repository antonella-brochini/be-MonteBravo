package com.monteBravo.be.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StockDTO {

    @NotNull(message = "Falata id usuario administrador")
    private long idUser;

    @NotNull(message = "Name may not be null")
    private long idProducto;

    @NotNull(message = "Cantidad debe ser mayor a 0")
    @Min(value = 1,  message = "La cantidad debe ser mayor a cero")
    private int cantidad;

    @NotNull(message = "Puede ser vacio pero no nullo")
    private String motivo;

    @NotNull(message = "Debe haber un signo + - ")
    @Min(0)
    @Max(1)
    private int signo;


    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUsuario) {
        this.idUser = idUsuario;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public long getIdProducto() {
        return idProducto;
    }


    public StockDTO(int signo, String motivo, int cantidad, long idProducto, long idUser) {
        this.signo = signo;
        this.motivo = motivo;
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.idUser = idUser;

    }


}
