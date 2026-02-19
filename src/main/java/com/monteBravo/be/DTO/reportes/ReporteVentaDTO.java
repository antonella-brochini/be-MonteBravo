package com.monteBravo.be.DTO.reportes;

public class ReporteVentaDTO {
    private String nombreUsuario;
    private Long idPedido;
    private Integer cantidad;
    private Double precio;
    private Long idProducto;
    private String nombreProducto;
    private String fechaCompra;


    // Constructor
    public ReporteVentaDTO(Object[] row) {
        this.nombreUsuario = (String) row[0];
        this.idPedido = ((Number) row[1]).longValue();
        this.cantidad = ((Number) row[2]).intValue();
        this.precio = ((Number) row[3]).doubleValue();
        this.idProducto = ((Number) row[4]).longValue();
        this.nombreProducto = (String) row[5];
        this.fechaCompra = row[6].toString();
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
