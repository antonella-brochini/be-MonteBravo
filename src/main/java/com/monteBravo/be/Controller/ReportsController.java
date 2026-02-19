package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.reportes.*;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.Services.ReporteService;
import com.monteBravo.be.entity.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reporte")
public class ReportsController {

    private final ReporteService reporteService;
    private final ProductoRepository productoRepository;

    public ReportsController(ReporteService reporteService, ProductoRepository productoRepository) {
        this.reporteService = reporteService;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/stock")
    public ResponseEntity<?> getProductosConStock(
            @RequestParam(required = false, defaultValue = "0") String stockMayorA,
            @RequestParam(required = false, defaultValue = "1000") String stockMenorA
    ) {
       try {
           int mayor = Integer.parseInt(stockMayorA);
           int menor = Integer.parseInt(stockMenorA);
           // Validaciones de los parámetros
           if (mayor < 0 || menor < 0) {
               return ResponseEntity.badRequest().body("Los valores de stock no pueden ser negativos.");
           }

           if (menor < mayor) {
               return ResponseEntity.badRequest().body("El valor de stock menor a no puede ser mayor que el stock mayor a.");
           }

           // Llamar al repositorio para obtener los productos
           List<Producto> productos = productoRepository.findProductosByStockRange(mayor, menor);

           return ResponseEntity.ok(productos);
       }catch(NumberFormatException e) {
           return ResponseEntity.badRequest().body("Los parametros deben ser numeros enteros");
       }
    }



    @PostMapping("/ventas")
    public ResponseEntity<?> getVentas(@RequestBody Map<String, Object> body) {
        String fechaInicio = body.get("fechaInicio") != null ? body.get("fechaInicio").toString() : null;
        String fechaFin = body.get("fechaFin") != null ? body.get("fechaFin").toString() : null;
        String categoria = body.get("categoria") != null ? body.get("categoria").toString() : null;
        String producto = body.get("producto")  != null ? body.get("producto").toString() : null;


        if(fechaInicio == null || fechaFin == null){
            return ResponseEntity.badRequest().body("los filtros de fecha son Obligatorios");
        }

        List<Object[]> resultados = reporteService.obtenerVentasPorRangoFechas(fechaInicio, fechaFin, categoria, producto);

        List<ReporteVentaDTO> datosTransformados = resultados.stream()
                .map(ReporteVentaDTO::new)
                .toList();

        return ResponseEntity.ok(datosTransformados);
    }

    @PostMapping("/masvendidos")
    public ResponseEntity<?> getProductosMasVendidos(@RequestBody Map<String, Object> body) {
        String fechaInicio = body.get("fechaInicio") != null ? body.get("fechaInicio").toString() : null;
        String fechaFin = body.get("fechaFin") != null ? body.get("fechaFin").toString() : null;
        String categoria = body.get("categoria") != null ? body.get("categoria").toString() : null;
        String limite = body.get("limite") != null ? body.get("limite").toString() : null;

        if(isNullorEmpty(fechaInicio) || isNullorEmpty(fechaFin)){
            return ResponseEntity.badRequest().body("los filtros de fecha son Obligatorios");
        }
        if (!isNullorEmpty(limite)) {
            try {
                int limiteInt = Integer.parseInt(limite);

                if (limiteInt < 1) {
                    return ResponseEntity.badRequest().body("El límite tiene que ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("El límite debe ser un número entero válido");
            }
        }
        List<Object[]> resultados = reporteService.obtenerProductosMasVendidos(fechaInicio, fechaFin, categoria, limite);
        List<ReporteMasVendidosDTO> datosTransformados = resultados.stream().map(ReporteMasVendidosDTO::new).toList();

        return ResponseEntity.ok(datosTransformados);
    }

    @PostMapping("/clientemascompras")
    public ResponseEntity<?> getClienteConMasCompras(@RequestBody Map<String, Object> body){
        String monto = body.get("monto") != null ? body.get("monto").toString() : null;
        String cantidad = body.get("cantidad") != null ? body.get("cantidad").toString() : null;
        String limite = body.get("limite") != null ? body.get("limite").toString() : null;
        if (!isNullorEmpty(cantidad)) {
            try {
                int cantidadInt = Integer.parseInt(cantidad);

                if (cantidadInt < 1) {
                    return ResponseEntity.badRequest().body("la cantidad tiene que ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("la cantidad debe ser un número entero válido");
            }
        }
        if (!isNullorEmpty(limite)) {
            try {
                int limiteInt = Integer.parseInt(limite);

                if (limiteInt < 1) {
                    return ResponseEntity.badRequest().body("El límite tiene que ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("El límite debe ser un número entero válido");
            }
        }
        if (!isNullorEmpty(monto)) {
            try {
                int montoInt = Integer.parseInt(monto);
                if (montoInt < 1) {
                    return ResponseEntity.badRequest().body("el monto tiene que ser mayor a 0");
                }
            }catch(NumberFormatException e){
                return ResponseEntity.badRequest().body("El monto debe ser un número entero válido");
            }
        }
        List<Object[]> resultados = reporteService.obtenerClientesConMasCompras(cantidad, monto, limite);
        List<ReporteClienteMasComprasDTO> datosTransformados = resultados.stream().map(ReporteClienteMasComprasDTO::new).toList();

        return ResponseEntity.ok(datosTransformados);
    }



    @PostMapping("/carritoabandonado")
    public ResponseEntity<?> getCarritosAbandonados(@RequestBody Map<String, Object> body){
        String monto = body.get("monto") != null ? body.get("monto").toString() : null;
        String fechaInicio = body.get("fechaInicio") != null ? body.get("fechaInicio").toString() : null;
        String fechaFin = body.get("fechaFin") != null ? body.get("fechaFin").toString() : null;

        if(isNullorEmpty(fechaInicio) || isNullorEmpty(fechaFin)){
            return ResponseEntity.badRequest().body("los filtros de fecha son Obligatorios");
        }
        if (!isNullorEmpty(monto)) {
            try {
                int montoInt = Integer.parseInt(monto);
                if (montoInt < 1) {
                    return ResponseEntity.badRequest().body("el monto tiene que ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("El monto debe ser un número entero válido");
            }
        }
        List<Object[]> resultados = reporteService.obtenerCarritosAbandonados(fechaInicio, fechaFin, monto);
        List<ReporteCarritoAbandonadoDTO> datosTransformados = resultados.stream().map(ReporteCarritoAbandonadoDTO::new).toList();

        return ResponseEntity.ok(datosTransformados);
    }

    @GetMapping("/historicouser")
    public ResponseEntity<?> getHistoricoUser() {
        List<Object[]> resultado = reporteService.obtenerEstadisticasUsuarios();
        if (!resultado.isEmpty()) {
            Object[] fila = resultado.get(0);
            long totalUsuarios = ((Number) fila[0]).longValue();
            long nuevosUsuariosUltimoMes = ((Number) fila[1]).longValue();
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsuarios", totalUsuarios);
            response.put("nuevosUsuariosUltimoMes", nuevosUsuariosUltimoMes);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay datos disponibles");
    }

    @GetMapping("/historicantpedido")
    public ResponseEntity<?> getHistoricoCantPedido() {
        List<Object[]> resultado = reporteService.obtenerEstadisticasPedidos();
        if (!resultado.isEmpty()) {
            Object[] fila = resultado.get(0);
            long totalPedidos = ((Number) fila[0]).longValue();
            long pedidosUltimoMes = ((Number) fila[1]).longValue();
            Map<String, Object> response = new HashMap<>();
            response.put("totalPedidos", totalPedidos);
            response.put("pedidosUltimoMes", pedidosUltimoMes);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay datos disponibles");
    }

    @GetMapping("/topventa")
    public ResponseEntity<?> getTopTresProductosMasVendidos() {
        List<Object[]> resultado = reporteService.obtenerTopTres();
        if (!resultado.isEmpty()) {
            List<TopTresProductosDTO> datosEnDto = resultado.stream().map(TopTresProductosDTO::new).toList();
            return ResponseEntity.ok(datosEnDto);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay datos disponibles");
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueMensual() {
        List<Object[]> resultado = reporteService.obtenerRevenueMensual();
        if (!resultado.isEmpty()) {
            List<RevenueDTO> datosEnDto = resultado.stream().map(RevenueDTO::new).toList();
            return ResponseEntity.ok(datosEnDto);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay datos disponibles");
    }


    private boolean isNullorEmpty(String var){
        return  var == null || var.trim().isEmpty()  ;
    }
    private boolean isNullorEmpty(Integer var){
        return  var == null;
    }

}
