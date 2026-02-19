package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.OpinionProductoDTO;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.OpinionProductoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.OpinionProducto;
import com.monteBravo.be.entity.Producto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/opinionProducto")
public class OpinionProductoController {

    private final OpinionProductoRepository opinionProductoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public OpinionProductoController(OpinionProductoRepository opinionProductoRepository, ClienteRepository clienteRepository, ProductoRepository productoRepository) {
        this.opinionProductoRepository = opinionProductoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;

    }
    @Operation(summary = "Agrega una opinion a un producto", description = "Agrega opinion al producto en base a un idUser, idProducto, cantidad de estrellas y una opinion")
    @PostMapping
    public ResponseEntity<?> crearOpinionProducto(@RequestBody Map<String, Object> OpinionProductoMap) {
        Long idUser = Long.valueOf(OpinionProductoMap.get("idUser").toString());
        Long idProducto = Long.valueOf(OpinionProductoMap.get("idProducto").toString());
        double estrellas = Double.parseDouble(OpinionProductoMap.get("estrellas").toString());

        if (estrellas < 0 || estrellas > 5) {
            return ResponseEntity.badRequest().body("Las estrellas deben estar entre 0 y 5.");
        }

        Cliente cliente = clienteRepository.findById(idUser).orElse(null);
        if (cliente == null) {
            return ResponseEntity.badRequest().body("No existe Usuario con el ID: " + idUser);
        }

        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("No existe Producto con el ID: " + idProducto);
        }

        OpinionProducto opinionProducto = new OpinionProducto(
                OpinionProductoMap.get("opinion").toString(),
                producto,
                cliente,
                estrellas
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(opinionProductoRepository.save(opinionProducto));
    }
    @Operation(summary = "Get all opiniones producto", description = "Devuelve todas las opiniones de todos los productos")
    @GetMapping
    public List<OpinionProducto> getAllOpinionProducto(){
        return opinionProductoRepository.findAll();
    }

    @Operation(summary = "Get all opiniones de un producto por ID", description = "Devuelve todas las opiniones para un producto particular")
    @GetMapping("/{idProducto}")
    public ResponseEntity<List<OpinionProductoDTO>> getOpinionesByProducto(@PathVariable Long idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<OpinionProductoDTO> opiniones = opinionProductoRepository.findByProductoId(idProducto).stream()
                .map(op -> new OpinionProductoDTO(
                        op.getOpinion(),
                        op.getEstrellas(),
                        op.getCliente().getNombreUsuario() // O email según preferencia
                ))
                .toList();

        return ResponseEntity.ok(opiniones);
    }


}
