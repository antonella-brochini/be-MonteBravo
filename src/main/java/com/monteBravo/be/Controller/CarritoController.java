package com.monteBravo.be.Controller;

import com.monteBravo.be.Repository.CarritoProductoRepository;
import com.monteBravo.be.Repository.CarritoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.Carrito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    @Autowired
    private CarritoProductoRepository carritoProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

   
    @GetMapping("/todos")
    @Operation(
            summary = "Obtener todos los carritos del sistema",
            description = "Permite obtener una lista completa de todos los carritos de compras registrados en el sistema. La operación devuelve los detalles de cada carrito, incluyendo los productos que contienen, las cantidades, el precio total y otros datos relevantes. Si no se encuentran carritos en el sistema, se devuelve una lista vacía."
    )
    public List<Carrito> getCarritos(){
         return carritoRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un carrito por ID",
            description = "Permite obtener los detalles completos de un carrito de compras utilizando su identificador único. La operación devuelve todos los productos en el carrito, su cantidad, el precio total y cualquier otro dato relevante. Si no se encuentra un carrito con el ID proporcionado, se devuelve un mensaje de error."
    )
    public ResponseEntity<Carrito> getCarritoPorId(@PathVariable Long id){
        return carritoRepository.findById(id)
                .map(carrito -> ResponseEntity.ok(carrito))
                .orElse(ResponseEntity.notFound().build());
    }

}


