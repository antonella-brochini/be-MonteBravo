package com.monteBravo.be.DTO.reportes;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Mapea filas de {@code obtenerRevenueMensual} (agregación mensual pedido / ped_productos).
 */
public class RevenueDTO {
    private static final DateTimeFormatter MES_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private String mes;
    private Double revenueMensual;
    private Long pedidosTotales;

    public RevenueDTO(Object[] resultado) {
        this.mes = formatoMes(resultado[0]);
        this.revenueMensual = resultado[1] == null ? null : ((Number) resultado[1]).doubleValue();
        this.pedidosTotales = resultado[2] == null ? null : ((Number) resultado[2]).longValue();
    }

    /**
     * PostgreSQL / JDBC puede devolver Timestamp, Date u otros tipos temporales; evita cast directo solo a Timestamp.
     */
    private static String formatoMes(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Timestamp ts) {
            return ts.toLocalDateTime().format(MES_FMT);
        }
        if (valor instanceof Date d) {
            return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()).format(MES_FMT);
        }
        if (valor instanceof LocalDateTime ldt) {
            return ldt.format(MES_FMT);
        }
        if (valor instanceof OffsetDateTime odt) {
            return odt.toLocalDateTime().format(MES_FMT);
        }
        if (valor instanceof Instant ins) {
            return LocalDateTime.ofInstant(ins, ZoneId.systemDefault()).format(MES_FMT);
        }
        return valor.toString();
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
