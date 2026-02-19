package com.monteBravo.be.DTO.emailDto;

import com.monteBravo.be.Inteface.IResolvedorVariable;

import java.util.Date;
import java.util.List;

public class NotificacacionAdmiDTO implements IResolvedorVariable{


    private String idMercadoPago;
    private String estado;
    private String detalleEstado;
    private Date fecha;
    private String emailCliente;
    private String celularCliente;

    private List<OrderItemDTO> orderItems; // {{#each orderItems}}
    public String getCelularCliente() {
        return celularCliente;
    }

    public void setCelularCliente(String celularCliente) {
        this.celularCliente = celularCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDetalleEstado() {
        return detalleEstado;
    }

    public void setDetalleEstado(String detalleEstado) {
        this.detalleEstado = detalleEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdMercadoPago() {
        return idMercadoPago;
    }

    public void setIdMercadoPago(String idMercadoPago) {
        this.idMercadoPago = idMercadoPago;
    }



    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public NotificacacionAdmiDTO(String celularCliente, String emailCliente, Date fecha, String detalleEstado, String estado, String idMercadoPago) {
        this.celularCliente = celularCliente;
        this.emailCliente = emailCliente;
        this.fecha = fecha;
        this.detalleEstado = detalleEstado;
        this.estado = estado;
        this.idMercadoPago = idMercadoPago;
    }
    public NotificacacionAdmiDTO(){}



    @Override
    public String devolverGetterPorNombre(String nombreVariable) {
        switch (nombreVariable.toLowerCase()) {
            case "idmercadopago":
                return getIdMercadoPago();
            case "estado":
                return getEstado();
            case "detalleestado":
                return getDetalleEstado();
            case "fecha":
                    return getFecha().toString();
            case "email":
                   return getEmailCliente();
            case "celular":
                   return getCelularCliente();
            default:
                return "";
        }
    }
}
