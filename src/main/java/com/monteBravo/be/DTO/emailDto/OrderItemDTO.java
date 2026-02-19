package com.monteBravo.be.DTO.emailDto;

import com.monteBravo.be.Inteface.IResolvedorVariable;

public class OrderItemDTO  implements IResolvedorVariable  {

    private String productName; // {{productName}}
    private int quantity; // {{quantity}}
    private double price; // {{price}}
    private double subtotal; // {{subtotal}}

    public OrderItemDTO(){}
    // Constructor
    public OrderItemDTO(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price * quantity;
    }

    // Getters y Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String devolverGetterPorNombre(String nombreProp) {
        switch (nombreProp.toLowerCase()) {
            case "productname":
                return this.getProductName();
            case "quantity":
                return String.valueOf(this.getQuantity());
            case "price":
                return String.valueOf(this.getPrice());
            case "subtotal":
                return String.valueOf(this.getSubtotal());
            default:
                System.out.println("Variable no encontrada: " + nombreProp);
                return "";
        }
    }

}

