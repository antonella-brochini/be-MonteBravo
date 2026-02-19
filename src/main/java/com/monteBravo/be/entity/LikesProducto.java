package com.monteBravo.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"id_user", "id_producto"}
                )
        }
)
public class LikesProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMeGusta;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
   private Producto producto;

    private boolean liked;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUpdate = new Date();

    public LikesProducto(Cliente cliente, Producto producto, boolean liked, Date fechaCreacion, Date fechaUpdate) {
        this.cliente = cliente;
        this.producto = producto;
        this.liked = liked;
        this.fechaCreacion = fechaCreacion;
        this.fechaUpdate = fechaUpdate;
    }

    public LikesProducto(){}

    public Long getIdMeGusta() {
        return idMeGusta;
    }

    public void setIdMeGusta(Long idMeGusta) {
        this.idMeGusta = idMeGusta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaUpdate() {
        return fechaUpdate;
    }

    public void setFechaUpdate(Date fechaUpdate) {
        this.fechaUpdate = fechaUpdate;
    }
}
