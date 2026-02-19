package com.monteBravo.be.Services;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;

import com.mercadopago.exceptions.MPException;

import com.monteBravo.be.DTO.EnvioDTO;
import com.monteBravo.be.entity.Carrito;
import com.monteBravo.be.entity.CarritoProducto;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;


@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;
    @Value("${mercadopago.claveSecretaMP}")
    private String claveSecretaMP;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }


    public Preference crearPreferenciaDePago(Carrito carrito, EnvioDTO envio) throws MPException, MPApiException {

    List<PreferenceItemRequest> items = new ArrayList<>();

    for(CarritoProducto item : carrito.getProductos()){
        String precioString = String.valueOf(item.getPrecio());
        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id( String.valueOf( item.getProducto().getIdProducto() ) )
                        .title(item.getProducto().getNombre())
                        .description(item.getProducto().getDescripcion())
                        .pictureUrl(item.getProducto().getImagen())
                        .categoryId(String.valueOf(item.getProducto().getCategoriaProducto().getIdCategoriaProducto()))
                        .quantity(item.getCantidad())
                        .currencyId("UY")
                        .unitPrice(new BigDecimal(precioString))
                        .build();
        items.add(itemRequest);
    }

    PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success("https://fe-montebravo.onrender.com/user/carrito/resultadoCompra/exito")
            .pending("https://fe-montebravo.onrender.com/user/carrito/resultadoCompra/pendiente")
            .failure("https://fe-montebravo.onrender.com/resultadoCompra/fallo")
            .build();


    Map<String,Object> metadata = new HashMap<>();

    if(carrito.getCliente()!=null){
        metadata.put("idUser", String.valueOf(carrito.getCliente().getIdUser()));
    }
        metadata.put("phone", envio.getCelular());
        metadata.put("email", envio.getEmail());
        if (envio.getSucursal().isEmpty()) {
            metadata.put("state", envio.getState());
            metadata.put("city", envio.getCity());
            metadata.put("zip_code", envio.getPostalCode());
            metadata.put("street_name", envio.getStreetName());
            metadata.put("street_number", envio.getStreetNumber());
            metadata.put("aclaracion",envio.getAclaracion());
            metadata.put("metodoEnvio",envio.getMetodoEnvio());
        }else{
            metadata.put("sucursal", envio.getSucursal());
        }

       // los pagos fisico se cancelan a los 3 dias
        OffsetDateTime expirationTimePago = OffsetDateTime.now(ZoneOffset.of("-03:00")).plusDays(3);
        // las links de pago vencen a los 45 minutos de emitidos
        OffsetDateTime expirationTimePreference = OffsetDateTime.now(ZoneOffset.of("-03:00")).plusMinutes(45);
        OffsetDateTime expirationTimePreferenceFrom = OffsetDateTime.now(ZoneOffset.of("-03:00"));
        // Comprador builder
        PreferencePayerRequest comprador = crearComprador(carrito, envio);
        // Envio builder
        PreferenceShipmentsRequest shipment = crearEnvio(envio);
        //Preference builder
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .payer(comprador)
                .shipments(shipment)
                .backUrls(backUrls).metadata(metadata).expires(true).dateOfExpiration(expirationTimePago).
                expires(true).expirationDateFrom(expirationTimePreferenceFrom).expirationDateTo(expirationTimePreference).externalReference("12345678").
                build();

        PreferenceClient client = new PreferenceClient();

    try{
       return client.create(preferenceRequest);

    }catch(MPApiException e){
        System.out.println("Error al comunicarse con MercadoPago: " + e.getApiResponse().getContent());
        throw e;
    }

    }


    public PreferenceShipmentsRequest crearEnvio(EnvioDTO envio){
        if(!envio.getSucursal().isEmpty()){
            PreferenceShipmentsRequest psr = PreferenceShipmentsRequest.builder()
                    .mode("not_specified")
                    .localPickup(true)
                    .build();
            return psr;

        }else{
            PreferenceShipmentsRequest psr = PreferenceShipmentsRequest.builder()
                    .mode("not_specified")
                    .localPickup(false)
                    .cost(BigDecimal.valueOf(envio.getCostoEnvio()))
                    .receiverAddress(
                            PreferenceReceiverAddressRequest.builder()
                                    .stateName(envio.getState())
                                    .cityName(envio.getCity())
                                    .zipCode(envio.getPostalCode())
                                    .streetName(envio.getStreetName())
                                    .streetNumber(envio.getStreetNumber())
                                    .build()).build();
            return psr;
        }

    }


    public PreferencePayerRequest crearComprador(Carrito carrito, EnvioDTO envio){

        if(carrito.getCliente()!=null){
            PreferencePayerRequest ppr = PreferencePayerRequest.builder()
                    .name(carrito.getCliente().getNombreUsuario())
                    .email(String.valueOf(envio.getEmail()))
                    .phone(PhoneRequest.builder().areaCode("598").number(envio.getCelular()).build())
                    .build();
            return ppr;
        }else{
            PreferencePayerRequest ppr = PreferencePayerRequest.builder()
                    .phone(PhoneRequest.builder().areaCode("598").number(envio.getCelular()).build())
                    .email(String.valueOf(envio.getEmail()))
                    .build();
            return ppr;

        }

    }

    public boolean verificarCliente(HttpServletRequest request, String paymentId) {
        // Obtener valores de encabezados usando HttpServletRequest
        String xSignature = request.getHeader("x-signature");
        String xRequestId = request.getHeader("x-request-id");

        // Separar 'x-signature' en partes
        String[] parts = xSignature.split(",");
        String ts = null;
        String v1 = null;

        // Iterar sobre los valores para obtener ts y v1
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                if ("ts".equals(key)) {
                    ts = value;
                } else if ("v1".equals(key)) {
                    v1 = value;
                }
            }
        }
        // Crear la cadena 'manifest'

        String signedTemplate = "id:" + paymentId + ";request-id:" + xRequestId + ";ts:" + ts + ";";
        String cyphedSignature = new HmacUtils("HmacSHA256", claveSecretaMP).hmacHex(signedTemplate);

        if (cyphedSignature.equals(v1))
            return true;
         else
            return false;

    }



}





