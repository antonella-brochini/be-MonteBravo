package com.monteBravo.be.DTO.reportes;


import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class RevenueDTO {
    private String mes;
    private Double revenueMensual;
    private Long pedidosTotales;

    public RevenueDTO(Object[] resultado) {
        this.mes = ((Timestamp) resultado[0])
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));
        this.revenueMensual = (Double) resultado[1];
        this.pedidosTotales = ((Number) resultado[2]).longValue();
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Double getRevenueMensual() {
        return revenueMensual;
    }

    public void setRevenueMensual(Double revenueMensual) {
        this.revenueMensual = revenueMensual;
    }

    public Long getPedidosTotales() {
        return pedidosTotales;
    }

    public void setPedidosTotales(Long pedidosTotales) {
        this.pedidosTotales = pedidosTotales;
    }

    @Override
    public String toString() {
        return "RevenueDTO{" +
                "mes='" + mes + '\'' +
                ", revenueMensual=" + revenueMensual +
                ", pedidosTotales=" + pedidosTotales +
                '}';
    }
}

