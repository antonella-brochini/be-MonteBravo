package com.monteBravo.be.Controller;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.monteBravo.be.DTO.*;
import com.monteBravo.be.Repository.CarritoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.Services.MercadoPagoGestionPagos;
import com.monteBravo.be.Services.MercadoPagoService;
import com.monteBravo.be.Services.MovStockService;
import com.monteBravo.be.Services.PagoService;
import com.monteBravo.be.entity.*;
import com.monteBravo.be.entity.Exception.StockException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/mp")
public class MPController {

    @Autowired
    private MercadoPagoService mercadoPagoService;
    @Autowired
    private MercadoPagoGestionPagos mercadoPagoGestionPagos;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private MovStockService movStockService;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private PagoService pagoService;

   // @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@PreAuthorize("permitAll()")
@PostMapping("/create_preference")
@Operation(
        summary = "Crear una preferencia de pago para MercadoPago",
        description = "Permite crear una preferencia de pago en MercadoPago, lo que incluye los detalles del pedido, como los productos, cantidades, precios, y el valor total. Esta operación devuelve una URL para redirigir al usuario a la plataforma de pago de MercadoPago, donde podrá completar la transacción. En caso de error, se proporciona un mensaje detallado sobre el problema."
)
public  ResponseEntity<?> crearPreferencia(@Valid @RequestBody PreferenciaDTO preferencia) {
    CarritoDTO carritoDTO = preferencia.getCarrito();
    EnvioDTO envioDTO = preferencia.getEnvio();
    long id = carritoDTO.getIdCarrito();
    List<CarritoProducto> lista;
    Carrito c = new Carrito();
    if (id == 0) {
        // el comprador no tiene usuario idcarrito = 0
        lista = manejarPago(carritoDTO);
        if(lista.isEmpty())
            //error status 400
            return ResponseEntity.badRequest().body("Error el carrito esta vacio");

        c.setProductos(lista);
    }else{
        c = carritoRepository.findById(id).orElse(null);
        if (c == null || c.getProductos().isEmpty()) {
            //error status 400
            return ResponseEntity.badRequest().body("Error el carrito esta vacio");
        }else{
            lista = c.getProductos();
        }
    }
    try {
            //compruebo si hay stock disponible de todos los productos, si no hay de alguno mando mensaje a front end
            try {
                List<MovimientoStock> listaMov = new ArrayList<>();
                for (CarritoProducto item : lista) {
                    MovimientoStock ms = new MovimientoStock(item.getProducto(), item.getCantidad(), 0);
                    listaMov.add(ms);
                }
                movStockService.PuedoMovStockSistema(listaMov);

            } catch (StockException e) {
                // responder al front end que no hay suficiente stcok
                // Responder con un código 422 Unprocessable Entity
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(e.getProducto().getIdProducto());
            }
            Preference p = mercadoPagoService.crearPreferenciaDePago(c,envioDTO);
            return ResponseEntity.ok(p.getInitPoint());

        } catch (MPException | MPApiException e) {
            e.getMessage();
            e.printStackTrace();
            // error status 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de Mercado Pago");

    }

    }


    private List<CarritoProducto>  manejarPago( CarritoDTO carrito){
        // el comprador no tiene usuario

        List<ItemCarritoDTO> items = carrito.getProductos();
        List<CarritoProducto> itemsSistema = new ArrayList<>();
        items.forEach(item -> {
            CarritoProducto cp = new CarritoProducto();
            cp.setCantidad(item.getCantidad());
            Optional<Producto> p = productoRepository.findById(item.getIdProducto());
            if (p.isPresent()) {
                cp.setProducto(p.get());
                cp.setPrecio(p.get().getPrecio());
                itemsSistema.add(cp);
            }
        });

        return itemsSistema;
    }


   @PreAuthorize("permitAll()")
   @PostMapping("/seguimiento_pagos")
   @Operation(
           summary = "Recive notificaciones de Mercado pago sobre el estado de las transacciones",
           description = "Segun el estado de la transaccion confirma o cancela el pago en el sistema."
   )
   public ResponseEntity<String> seguimientoDePagos(@RequestBody Map<String, Object> notification , HttpServletRequest request){
       Object type = notification.get("type");
       String tipo = (String) type;
       Map<String, Object> data = (Map<String, Object>) notification.get("data");
       String ID = (String) data.get("id");

       if(mercadoPagoService.verificarCliente(request,ID)) {
            if ("payment".equals(tipo)) {
                CompletableFuture.runAsync(() -> mercadoPagoGestionPagos.consultarPago(ID));
            }
           return ResponseEntity.status(200).body(null);
       }else
           return ResponseEntity.status(500).body(null);


}







}
