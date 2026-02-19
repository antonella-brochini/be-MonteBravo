package com.monteBravo.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCarrito;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "idUser")
    private Cliente cliente;
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL)
    private List<CarritoProducto> productos;
    private Date UltimaInteraccion; // dato agregado para reporte abandono de carrito

    public Carrito(Cliente cliente, List<CarritoProducto> productos, Date UltimaInteraccion) {
        this.cliente = cliente;
        this.productos = productos;
        this.UltimaInteraccion = UltimaInteraccion;
    }

    public Date getUltimaInteraccion() {
        return UltimaInteraccion;
    }

    public void setUltimaInteraccion(Date ultimaInteraccion) {
        UltimaInteraccion = ultimaInteraccion;
    }

    public Carrito(){}

    public long getIdCarrito() {
        return idCarrito;
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<CarritoProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<CarritoProducto> productos) {
        this.productos = productos;
    }


    public void borrarCarritoProductos(){



    }
}



