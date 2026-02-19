package com.monteBravo.be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AlertaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;

    private String mensaje;
    private boolean resuelta;
    private LocalDateTime fechaCreacion;

    public AlertaStock(Producto producto, String mensaje) {
        this.producto = producto;
        this.mensaje = mensaje;
        this.resuelta = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public AlertaStock() {
        this.resuelta = false;
        this.fechaCreacion = LocalDateTime.now();
    }
}
