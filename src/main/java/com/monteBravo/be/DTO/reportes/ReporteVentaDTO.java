package com.monteBravo.be.DTO.reportes;

/**
 * Mapea filas de {@code obtenerVentasPorRangoFechas} (vista {@code reporte_vta}).
 */
public class ReporteVentaDTO {
    private String nombreUsuario;
    private Long idPedido;
    private Integer cantidad;
    private Double precio;
    private Long idProducto;
    private String nombreProducto;
    private String fechaCompra;

    public ReporteVentaDTO(Object[] row) {
        this.nombreUsuario = row[0] == null ? null : row[0].toString();
        this.idPedido = row[1] == null ? null : ((Number) row[1]).longValue();
        this.cantidad = row[2] == null ? null : ((Number) row[2]).intValue();
        this.precio = row[3] == null ? null : ((Number) row[3]).doubleValue();
        this.idProducto = row[4] == null ? null : ((Number) row[4]).longValue();
        this.nombreProducto = row[5] == null ? null : row[5].toString();
        this.fechaCompra = row[6] == null ? null : row[6].toString();
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
