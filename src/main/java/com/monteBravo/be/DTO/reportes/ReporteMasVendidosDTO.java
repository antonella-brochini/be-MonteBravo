package com.monteBravo.be.DTO.reportes;

public class ReporteMasVendidosDTO {
    private String nombreProducto;
    private int totalCantidad;
    private double totalIngresos;
    private String fechaCompra;
    private String categoria;
    private Long idProducto;

    public ReporteMasVendidosDTO(Object[] row) {
        this.nombreProducto =  row[0].toString();
        this.totalCantidad = ((Number) row[1]).intValue();
        this.totalIngresos = ((Number) row[2]).doubleValue();
        this.fechaCompra = row[3].toString();
        this.categoria =  String.valueOf(row[4]);
        this.idProducto = ((Number) row[5] ).longValue();
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getTotalCantidad() {
        return totalCantidad;
    }

    public void setTotalCantidad(int totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
