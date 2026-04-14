package com.monteBravo.be.DTO.reportes;

/**
 * Mapea filas de {@code obtenerClientesConMasCompras} (vista {@code reporte_clientes_compras}).
 */
public class ReporteClienteMasComprasDTO {

    private long idCliente;
    private String nombreCliente;
    private long totalCompras;
    private Double totalGastos;

    public ReporteClienteMasComprasDTO(Object[] row) {
        this.idCliente = row[0] == null ? 0L : ((Number) row[0]).longValue();
        this.nombreCliente = row[1] == null ? null : row[1].toString();
        this.totalCompras = row[2] == null ? 0L : ((Number) row[2]).longValue();
        this.totalGastos = row[3] == null ? null : ((Number) row[3]).doubleValue();
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
