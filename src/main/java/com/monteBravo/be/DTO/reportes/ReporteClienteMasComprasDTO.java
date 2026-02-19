package com.monteBravo.be.DTO.reportes;

public class ReporteClienteMasComprasDTO {

    private long idCliente;
    private String nombreCliente;
    private long totalCompras;
    private Double totalGastos;

    public ReporteClienteMasComprasDTO(Object[] row){
        this.idCliente = (long) row[0];
        this.nombreCliente = (String) row[1];
        this.totalCompras = (long) row[2];
        this.totalGastos = (Double) row[3];
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public long getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(long totalCompras) {
        this.totalCompras = totalCompras;
    }

    public Double getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(Double totalGastos) {
        this.totalGastos = totalGastos;
    }
}
