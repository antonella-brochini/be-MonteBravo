package com.monteBravo.be.Services;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import com.monteBravo.be.Exceptions.PaymentException;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.PagoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MercadoPagoGestionPagos {
    @Autowired
    private MovStockService movStockService;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private  ClienteRepository clienteRepository;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private CarritoProductoService carritoProductoService;

    @Autowired
    private SendEmail emailService;



    public void consultarPago(String id ) {

        try {

            PaymentClient paymentClient = new PaymentClient();
            long paymentID = Long.parseLong(id);
            Payment payment = paymentClient.get(paymentID);

            if (elPagoYafueProcesado(payment)) {
               System.out.print("el pago ya fue procesado");
                return;
            }
            switch (payment.getStatus()) {
                case "pending":
                    System.out.println("El pago está pendiente. El usuario aún no ha completado el proceso.");
                    // pago red pagos abitab cambia a estado cancelled or approved
                    manejarPagoPendiente(payment);

                    break;
                case "approved":
                    manejarPagoAprobado(payment);
                    break;
                case "authorized":
                    System.out.println("El pago ha sido autorizado pero aún no se ha capturado.");
                    break;
                case "in_process":
                    System.out.println("El pago está en proceso de revisión.");
                    // Por una falta de procesamiento online, el pago está siendo procesado de manera offline. cambia el estado a cancelled o aproved
                    // reservo stock

                    manejarPagoEnProceso(payment);
                    break;
                case "in_mediation":
                    System.out.println("El usuario ha iniciado una disputa.");
                    manejarPagoEnMediacion(payment);
                    break;
                case "rejected":
                    System.out.println("El pago fue rechazado.");
                    manejarPagoRechazado(payment);
                    break;
                case "cancelled":
                    System.out.println("El pago fue cancelado.");
                    // a los 30 dias un pago exipira y pasa a cancelado
                    // subo stock
                    manejarPagoCancelado(payment);
                    break;
                case "refunded":
                    System.out.println("El pago fue reembolsado al usuario.");
                    // se gestiona por afuera de la app
                    notificarAdmin(payment);
                    break;
                case "charged_back":
                    System.out.println("Se realizó un contracargo en la tarjeta de crédito.");
                    // se gestiona por afuera de la app
                    notificarAdmin(payment);
                    break;
                default:
                    System.out.println("Estado del pago desconocido.");

                    break;
            }


        } catch (MPApiException e) {

            System.err.println("Error en la API de Mercado Pago: " + e.getApiResponse().getContent());
        } catch (MPException e) {

            System.out.println("Error inesperado: " + e.getMessage());
        }catch (NumberFormatException e) {
            System.out.println("ID de MercadoPago inválido: " + e.getMessage());
        }


    }

    private boolean elPagoYafueProcesado(Payment payment){
        Optional <Pago> pagoViejo = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
        if(pagoViejo.isPresent()){
            if(pagoViejo.get().getEstado().equals(payment.getStatus()) && pagoViejo.get().getStatusDetail().equals(payment.getStatusDetail())){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public void moverStock( Payment payment,int signo){
        // Recorremos los items del pago
        List<PaymentItem> items = payment.getAdditionalInfo().getItems();
        Map<String, Object> metadata = payment.getMetadata();
        System.out.println("metadata "+ metadata);
        Cliente c = new Cliente();
        if (metadata.get("id_user")!=null) {
            try{
                Object userIdObj = metadata.get("id_user");
                String userIdStr = (String) userIdObj;
                Long userId = Long.parseLong(userIdStr);
                Optional<Cliente>  cliente =  clienteRepository.findById(userId);
                if (cliente.isPresent())
                    c = cliente.get();
            }catch(NumberFormatException e ){
                System.out.println("ID de MercadoPago inválido: " + e.getMessage());
            }
        }
        //si no hay cliente inserto null en base de datos
        if (c.getIdUser() == 0)
          c=null;

        if (!items.isEmpty()) {
            List<MovimientoStock> listaMov = new ArrayList<>();

            // Recorrer los items y creo MovStock
            for (PaymentItem item : items) {
                int quantity = item.getQuantity();
                String idProducto = item.getId();
                long idProductoLong = Long.parseLong(idProducto);
                Optional<Producto> p = productoRepository.findById(idProductoLong);
                if (p.isPresent()) {
                    Producto producto = p.get();
                    MovimientoStock ms = new MovimientoStock(producto, quantity, signo);
                    ms.setUsuario(c);
                    String motivo = (signo == 0) ? "Compra web" : "El pago fue rechazado o cancelado";
                    ms.setMotivo(motivo);
                    listaMov.add(ms);
                }
            }
            // muevo el stock
            List<MovimientoStock> listaSinStock =  movStockService.MoverStockSistema(listaMov);
            if(!listaSinStock.isEmpty())
                //sino hay stock notifico al admin
                notificarAdmiErrorDeStock(payment,listaSinStock);
        } else {
            System.out.println("No se encontraron items en el pago.");
        }
    }

    private void enviarNotificacion( Payment payment){
        emailService.notificarClienteEstadoPago(payment);

    }

    private void notificarAdmin(Payment payment){
        emailService.notificarAdmin(payment);
    }

    public void notificarAdmiErrorDeStock(Payment payment, List<MovimientoStock> listaSinStock){
        emailService.notificarAdminSobreventa(payment ,listaSinStock);
    }

    private void crearPedido( Payment payment){

            Pedido pedido = new Pedido();
            List<PedidoProducto> itemsPedido = new ArrayList<>();
            List<PaymentItem> itemsPayment = payment.getAdditionalInfo().getItems();
            for (PaymentItem item : itemsPayment) {
                int quantity = item.getQuantity();
                String idProducto = item.getId();
                BigDecimal precio = item.getUnitPrice();
                double doubleValue = precio.doubleValue();
                long idProductoLong = Long.parseLong(idProducto);
                Optional<Producto> p = productoRepository.findById(idProductoLong);
                if (p.isPresent()) {
                    PedidoProducto pd = new PedidoProducto();
                    pd.setProducto(p.get());
                    pd.setCantidad(quantity);
                    pd.setPrecio(doubleValue);
                    itemsPedido.add(pd);
                }
            }

            pedido.setPedidosProducto(itemsPedido);
            Map<String, Object> metadata = payment.getMetadata();
            Object phoneObj = metadata.get("phone");
            if (phoneObj != null) {
                pedido.setCelular(phoneObj.toString());
            }
            Object emailObj = metadata.get("email");
            if (emailObj != null) {
                pedido.setEmail(emailObj.toString());
            }

            pedido.setIdMercadoPago(payment.getId().toString());
            // envio
            Object sucursal = metadata.get("sucursal");
            if (sucursal != null) {
                pedido.setLocalPickUp(true);
            } else {

                String state = metadata.get("state") != null ? metadata.get("state").toString() : "Sin especificar";
                String city = metadata.get("city") != null ? metadata.get("city").toString() : "Sin especificar";
                String zipCode = metadata.get("zip_code") != null ? metadata.get("zip_code").toString() : "Sin especificar";
                String streetName = metadata.get("street_name") != null ? metadata.get("street_name").toString() : "Sin especificar";
                String streetNumber = metadata.get("street_number") != null ? metadata.get("street_number").toString() : "Sin especificar";
                String aclaracion = metadata.get("aclaracion") != null ? metadata.get("aclaracion").toString() : "Sin especificar";
                String metodoEnvio = metadata.get("metodo_envio") != null ? metadata.get("metodo_envio").toString() : "Sin especificar";
                pedido.setState(state);
                pedido.setCity(city);
                pedido.setPostalCode(zipCode);
                pedido.setStreet(streetName);
                pedido.setStreetNumber(streetNumber);
                pedido.setCosto(payment.getShippingAmount().doubleValue());
                pedido.setAclaracion(aclaracion);
                pedido.setEnvio(metodoEnvio);

            }
            Long idCarrito = null;
            if (metadata.get("id_user") != null) {
                try {
                    Object userIdObj = metadata.get("id_user");
                    String userIdStr = (String) userIdObj;
                    Long userId = Long.parseLong(userIdStr);
                    Optional<Cliente> cliente = clienteRepository.findById(userId);
                    if (cliente.isPresent()) {
                        pedido.setCliente(cliente.get());
                        idCarrito = cliente.get().getCarrito().getIdCarrito();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ID de MercadoPago inválido: " + e.getMessage());
                }
            } else {
                pedido.setCliente(null);
            }
            Pedido p = pedidoService.crearPedido(pedido);
            if (idCarrito != null) {
                carritoProductoService.borrarTodosLosCarritoProductos(idCarrito);
            }

    }






// manejo estados de consultarPagos
    private void manejarPagoPendiente(Payment payment){
            Optional<Pago> pagoViejo  = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
            if(pagoViejo.isPresent()) {
                actualizarPago(pagoViejo.get(),payment);
            }
            else{
                crearPago(payment);
                moverStock(payment,0);
                enviarNotificacion(payment);
            }

    }

    private void manejarPagoAprobado(Payment payment){
            //busco el pago si lo encuentro y es pendiente o en proceso no muevo stock si muevo pedido y email
            Optional <Pago> pagoViejo = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
            if(pagoViejo.isPresent()){

                if(pagoViejo.get().getEstado().equals("pending") || pagoViejo.get().getEstado().equals("in_process")){
                    crearPedido(payment);
                    actualizarPago(pagoViejo.get(),payment);
                    enviarNotificacion(payment);
                }else{
                    System.out.println("primero fue rechazado y ahora fue aprobado no exite registro de pago rechazado porque tiene un id diferente al de aprobado");
                }
            }else{
                // no existe pago, muevo stock, creo pedido, envio notificacion y guardo pago en base de datos
                crearPago(payment);
                moverStock(payment,0);
                crearPedido(payment);
                enviarNotificacion(payment);

            }

    }

    private void manejarPagoEnProceso(Payment payment){
        Optional<Pago> pagoViejo = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
        if(pagoViejo.isPresent()){
            actualizarPago(pagoViejo.get(),payment);
        }else{
            moverStock(payment,0);
            enviarNotificacion(payment);
            crearPago(payment);
        }

    }

    private void manejarPagoCancelado(Payment payment) {

        //busco el pago si lo encuentro y es pendiente subo el stock porque significa que expiro
        Optional<Pago> pagoViejo = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
        if (pagoViejo.isPresent()) {
            if (pagoViejo.get().getEstado().equals("pending") || pagoViejo.get().getEstado().equals("in_process")) {
                moverStock(payment, 1);
                actualizarPago(pagoViejo.get(),payment);
            }
        }
  }

    private void manejarPagoEnMediacion(Payment payment){
        Optional<Pago> pagoViejo= pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
        if(pagoViejo.isPresent()) {
            actualizarPago(pagoViejo.get(),payment);
        }

    }

    private void manejarPagoRechazado(Payment payment){

            Optional<Pago> pagoViejo = pagoService.obtenerPagoPorIdMercadoPago(payment.getId());
            if(pagoViejo.isPresent()) {
                actualizarPago(pagoViejo.get(),payment);
                moverStock(payment, 1);
            }
    }


//metodos auxiliares
    private void actualizarPago(Pago pagoViejo, Payment payment ){
    pagoViejo.setEstado(payment.getStatus());
    pagoViejo.setStatusDetail(payment.getStatusDetail());
    LocalDateTime ahora = LocalDateTime.now();
    ZoneId zona = ZoneId.of("America/Montevideo");
    ZonedDateTime fechaConZona = ahora.atZone(zona);
    pagoViejo.setUltimaActualizacion(fechaConZona.toLocalDateTime());
    pagoService.guardar(pagoViejo);
}

    private void crearPago(Payment payment){
    Pago pagoNuevo = new Pago();
    pagoNuevo.setEstado(payment.getStatus());
    pagoNuevo.setIdMercadoPago(payment.getId());
    pagoNuevo.setStatusDetail(payment.getStatusDetail());
    LocalDateTime ahora = LocalDateTime.now();
    ZoneId zona = ZoneId.of("America/Montevideo");
    ZonedDateTime fechaConZona = ahora.atZone(zona);
    pagoNuevo.setUltimaActualizacion(fechaConZona.toLocalDateTime());
    Pago p = pagoService.guardar(pagoNuevo);
}








}

