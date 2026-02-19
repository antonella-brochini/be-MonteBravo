package com.monteBravo.be.DTO.emailDto;

import com.monteBravo.be.Inteface.IResolvedorVariable;
import com.monteBravo.be.entity.Cliente;

import javax.imageio.event.IIOReadProgressListener;
import java.util.Date;
import java.util.List;

public class OrderDTO implements IResolvedorVariable {

    private String orderId; // {{orderId}}
    private Cliente cliente;
    private Date paymentDate; // {{paymentDate}}
    private double totalAmount; // {{totalAmount}}
    private List<OrderItemDTO> orderItems; // {{#each orderItems}}
    private double costoEnvio;

    public OrderDTO(){}
    // Constructor
    public OrderDTO(String orderId, Date paymentDate, double totalAmount, List<OrderItemDTO> orderItems, Cliente cliente ,double costoEnvio) {
        this.orderId = orderId;
        this.paymentDate = paymentDate;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.cliente = cliente;
        this.costoEnvio=costoEnvio;
    }

    // Getters y Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(double costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    @Override
    public String devolverGetterPorNombre(String nombreProp) {
        switch (nombreProp.toLowerCase()) {
            case "orderid":
                return this.getOrderId();
            case "paymentdate":
                return this.getPaymentDate() != null ? this.getPaymentDate().toString() : "";
            case "totalamount":
                return String.valueOf(this.getTotalAmount());
            case "costoenvio":
                return String.valueOf(this.getCostoEnvio());
            default:
                System.out.println("Variable no encontrada: " + nombreProp);
                return "";
        }
    }

}
