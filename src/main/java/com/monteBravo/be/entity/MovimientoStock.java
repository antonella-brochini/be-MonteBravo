package com.monteBravo.be.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "mov_stk")
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idMovStk;
    @ManyToOne
    @JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = true)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
    @Column(name = "producto_stock")
    private int productoStock;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;
    private int signo; // valores 1 o -1

    public MovimientoStock(Usuario usuario, Producto producto, int cantidad, LocalDateTime fecha, String motivo, int signo, int productoStock) {
        this.usuario = usuario;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
        this.signo = signo;
        this.productoStock = productoStock;
    }

public MovimientoStock(Producto producto, int cantidad, int signo){
    this.producto = producto;
    this.cantidad = cantidad;
    this.signo = signo;
}

    public MovimientoStock() {

    }

    public long getIdMovStk() {
        return idMovStk;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getSigno() {
        return signo;
    }

    public void setSigno(int signo) {
        this.signo = signo;
    }

    public int getProductoStock() {
        return productoStock;
    }

    public void setProductoStock(int productoStock) {
        this.productoStock = productoStock;
    }

    public void setIdMovStk(long idMovStk) {
        this.idMovStk = idMovStk;
    }

    @Override
    public String toString() {
        return "MovimientoStock {" +
                "idMovStk=" + idMovStk +
                ", usuario=" + usuario +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", motivo='" + motivo + '\'' +
                ", signo=" + signo +
                '}';
    }
// Es usado para actualizar
    public MovimientoStock(Producto producto, int cantidad, String motivo, int signo) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.signo = signo;
    }

    public boolean actualizar(MovimientoStock movActualizado) {
        // puede estar vacio
            setMotivo(movActualizado.getMotivo());
        // cambio de producto
        if(!getProducto().equals(movActualizado.getProducto())) {

            if(getProducto().puedoMoverStock(getCantidad(),getSigno() == 1 ? 0 : 1) && movActualizado.getProducto().puedoMoverStock(movActualizado.getCantidad(), movActualizado.getSigno()) ){
                // muevo stock producto nuevo
                movActualizado.getProducto().moverStock(movActualizado.getCantidad(), movActualizado.getSigno() );
                // deshago stock producto viejo
                getProducto().moverStock(getCantidad(), getSigno() == 1 ? 0 : 1);
                return true;
            }
            return false;
        }else{
        // si es el mismo producto deshago el movimiento anterior y hago uno nuevo
            //me aseguro que la modificacion es valida
            int signoDesaherStock = getSigno() == 1 ? 0 : 1;
            int cantidadADesaher = getCantidad();
            int cantidadNueva = movActualizado.getCantidad();
            int signoNuevo = movActualizado.getSigno();
            boolean resultado = false;
            // ejemplo stock +10 se equivoco de signo lo cambia a -10
            if (signoDesaherStock==signoNuevo)
                resultado = getProducto().puedoMoverStock( cantidadADesaher+cantidadNueva, signoDesaherStock );
            else {
                //signo desaher suma signoNuevo resta
                //ejemplo1 es -10 el stock se equivoco de cantidad y lo cambia a -15 , debo sumar 10 y restar 15
                //ejemplo2 es -15 el stock se equivoco y lo cambia a -10
                if (signoDesaherStock == 1) {
                    int cantidadCalculada = cantidadNueva - cantidadADesaher;
                    resultado = getProducto().puedoMoverStock(cantidadCalculada, signoNuevo);
                }
                else {
                // signo desaher resta signoNuevo suma
                // ejemplo1 es +10 el stock , se equivoca y lo cambia a +15
                // ejemplo2 es +15 el stock , se equivoca y lo cambia a +10
                    int cantidadCalculada = cantidadADesaher - cantidadNueva ;
                            resultado = getProducto().puedoMoverStock(cantidadCalculada  ,signoDesaherStock );
                }
            }
            if(resultado) {
                //dehago el stock viejo y  hago el nuevo
                getProducto().moverStock(getCantidad(), signoDesaherStock);
                getProducto().moverStock(movActualizado.getCantidad(), movActualizado.getSigno());

                return true;

            }else return false;
        }
    }



}
