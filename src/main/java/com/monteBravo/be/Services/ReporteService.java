package com.monteBravo.be.Services;

import com.monteBravo.be.Repository.ReporteRepository;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<Object[]> obtenerVentasPorRangoFechas(String fechaInicio, String fechaFin, String categoria, String producto) {
        StringBuilder consulta = new StringBuilder("""
        SELECT reporte_vta.nombre_usuario, reporte_vta.id_pedido, reporte_vta.cantidad, reporte_vta.precio, reporte_vta.id_Producto, reporte_vta.nombre_producto,
        reporte_vta.fecha_compra
        FROM reporte_vta
        WHERE 1=1
    """);

        Map<String, Object> parametros = new HashMap<>();

        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            consulta.append(" AND reporte_vta.fecha_compra >= :fechaInicio");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
            parametros.put("fechaInicio", java.sql.Date.valueOf(inicio));
        }

        if (fechaFin != null && !fechaFin.isEmpty()) {
            consulta.append(" AND reporte_vta.fecha_compra <= :fechaFin");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate fin = LocalDate.parse(fechaFin, formatter);
            parametros.put("fechaFin", java.sql.Date.valueOf(fin));
        }

        if(categoria != null && !categoria.isEmpty()) {
            consulta.append(" AND reporte_vta.categoria = :categoria");
            parametros.put("categoria", Integer.valueOf(categoria));
        }
        if(producto != null && !producto.isEmpty()) {
            consulta.append(" AND reporte_vta.id_producto = :producto");
            parametros.put("producto", Integer.valueOf(producto));
        }

        consulta.append(" ORDER BY reporte_vta.fecha_compra");

        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    public List<Object[]> obtenerProductosMasVendidos(String fechaInicio, String fechaFin, String categoria, String limite) {
        StringBuilder consulta = new StringBuilder("""
            SELECT pmv.nombre_producto, pmv.total_cantidad, pmv.total_ingresos, pmv.fecha_compra, pmv.categoria, pmv.id_producto FROM productos_mas_vendidos pmv
            WHERE 1=1
        """);
        Map<String, Object> parametros = new HashMap<>();

        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            consulta.append(" AND pmv.fecha_compra >= :fechaInicio");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
            parametros.put("fechaInicio", java.sql.Date.valueOf(inicio));
        }

        if (fechaFin != null && !fechaFin.isEmpty()) {
            consulta.append(" AND pmv.fecha_compra <= :fechaFin");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate fin = LocalDate.parse(fechaFin, formatter);
            parametros.put("fechaFin", java.sql.Date.valueOf(fin));
        }

        if(categoria != null && !categoria.isEmpty()) {
            consulta.append(" AND pmv.categoria = :categoria");
            parametros.put("categoria", Integer.valueOf(categoria));
        }
        consulta.append(" ORDER BY pmv.fecha_compra");

        if(limite != null && !limite.isEmpty()) {
            consulta.append(" LIMIT :limite");
            parametros.put("limite", Integer.valueOf(limite));
        }



        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);

    }

    public List<Object[]> obtenerClientesConMasCompras(String cantidad, String monto, String limite){
        StringBuilder consulta = new StringBuilder("""
            SELECT rcc.id_cliente, rcc.nombre_cliente, rcc.total_compras, rcc.total_gasto FROM reporte_clientes_compras rcc
            WHERE 1=1
        """);

        Map<String, Object> parametros = new HashMap<>();

        if(monto != null && !monto.isEmpty()) {
            consulta.append(" AND rcc.total_gasto >= :monto");
            parametros.put("monto", Integer.valueOf(monto));
        }

        if(cantidad != null && !cantidad.isEmpty()) {
            consulta.append(" AND rcc.total_compras >= :cantidad");
            parametros.put("cantidad", Integer.valueOf(cantidad));
        }

        if(limite != null && !limite.isEmpty()) {
            consulta.append(" LIMIT :limite");
            parametros.put("limite", Integer.valueOf(limite));
        }
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    public List<Object[]> obtenerCarritosAbandonados(String fechaInicio, String fechaFin, String monto) {
        StringBuilder consulta = new StringBuilder("""
            SELECT ca.id_carrito, ca.id_user, ca.ultima_interaccion, ca.nombre_usuario, ca.email, ca.total FROM carrito_abandonado ca
            WHERE 1=1
        """);

        Map<String, Object> parametros = new HashMap<>();

        if (!isNullorEmpty(fechaInicio)) {
            consulta.append(" AND ca.ultima_interaccion >= :fechaInicio");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
            parametros.put("fechaInicio", java.sql.Date.valueOf(inicio));
        }

        if (!isNullorEmpty(fechaFin)) {
            consulta.append(" AND ca.ultima_interaccion <= :fechaFin");
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate fin = LocalDate.parse(fechaFin, formatter);
            parametros.put("fechaFin", java.sql.Date.valueOf(fin));
        }

        if(!isNullorEmpty(monto)){
                consulta.append(" AND ca.total >= :monto");
                parametros.put("monto", Integer.valueOf(monto));

        }
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);

    }

    public List<Object[]> obtenerEstadisticasUsuarios() {
        StringBuilder consulta = new StringBuilder("""
        SELECT COUNT(*) AS total_usuarios, COUNT(CASE WHEN u.created_date >= (CURRENT_DATE - INTERVAL '1 month') THEN 1 END) AS nuevos_usuarios_ultimo_mes
        FROM usuario u WHERE u.id_user NOT IN (SELECT id_user FROM ADMINISTRADORES)
    """);
        Map<String, Object> parametros = new HashMap<>();
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    public List<Object[]> obtenerEstadisticasPedidos() {
        StringBuilder consulta = new StringBuilder("""
        SELECT COUNT(*) AS cantidad_pedidos, COUNT(CASE WHEN fecha_compra >= CURRENT_DATE - INTERVAL '1 month' THEN 1 END) AS cantped_mes
        FROM PEDIDO
    """);
        Map<String, Object> parametros = new HashMap<>();
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    public List<Object[]> obtenerTopTres() {
        StringBuilder consulta = new StringBuilder("""
        SELECT P.NOMBRE as nombre, COUNT(PR.ID_PRODUCTO) AS cantidad FROM PRODUCTOS P INNER JOIN PED_PRODUCTOS PR ON P.ID_PRODUCTO = PR.ID_PRODUCTO
        GROUP BY P.NOMBRE ORDER BY cantidad DESC LIMIT 3
    """);
        Map<String, Object> parametros = new HashMap<>();
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    public List<Object[]> obtenerRevenueMensual(){
        StringBuilder consulta = new StringBuilder("""
                SELECT
                    DATE_TRUNC('month', p.fecha_compra) AS mes,
                    SUM(d.cantidad * d.precio) AS revenue_mensual,
                    COUNT(p.id_pedido) AS pedidos_totales
                FROM
                    pedido p
                INNER JOIN
                    ped_productos d ON p.id_pedido = d.id_pedido
                GROUP BY
                    DATE_TRUNC('month', p.fecha_compra)
                ORDER BY
                    mes
            """);
        Map<String, Object> parametros = new HashMap<>();
        return reporteRepository.ejecutarConsulta(consulta.toString(), parametros);
    }

    private boolean isNullorEmpty(String var){
        return  var == null || var.trim().isEmpty()  ;
    }
}
