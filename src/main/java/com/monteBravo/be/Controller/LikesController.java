package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.LikesProductosDTO;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.LikesRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.LikesProducto;
import com.monteBravo.be.entity.Producto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/likes")
@Tag(name = "Lista de Me Gusta", description = "Controlador para gestionar los me gusta de productos")
public class LikesController {

    private final LikesRepository likesRepository;
    private final ProductoRepository productorepository;
    private final ClienteRepository clienterepository;


    public LikesController(LikesRepository likesRepository, ProductoRepository productorepository, ClienteRepository clienterepository) {
        this.likesRepository = likesRepository;
        this.productorepository = productorepository;
        this.clienterepository = clienterepository;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los me gusta", description = "Retorna una lista de todos los me gusta.")
    public ResponseEntity<List<LikesProducto>> getAllLikes() {
        List<LikesProducto> likes = likesRepository.findAll();
        return ResponseEntity.ok(likes);
    }

    @PostMapping
    @Operation(summary = "Agregar o actualizar un me gusta", description = "Agrega un nuevo me gusta o actualiza uno existente.")
    public ResponseEntity<?> addOrUpdateLike(@RequestBody Map<String, Object> likeData) {
        Long clienteId = Long.valueOf(likeData.get("clienteId").toString());
        Long productoId = Long.valueOf(likeData.get("productoId").toString());
        boolean liked = Boolean.parseBoolean(likeData.get("liked").toString());

        Cliente cliente = clienterepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No existe un cliente con ese Id");
        }


        Producto producto = productorepository.findById(productoId).orElse(null);
        if (producto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No existe un producto con ese Id");
        }

        Optional<LikesProducto> existingLike = likesRepository.findByClienteIdAndProductoId(clienteId, productoId);

        LikesProducto like;
        if (existingLike.isPresent()) {
            like = existingLike.get();
            like.setLiked(liked);
            like.setFechaUpdate(new Date());
        } else {
            like = new LikesProducto(cliente, producto, liked, new Date(), new Date());
        }
        LikesProducto savedLike = likesRepository.save(like);
        return ResponseEntity.ok(savedLike);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener un me gusta por ID", description = "Retorna un me gusta específico por su ID.")
    public ResponseEntity<LikesProducto> getLikeById(@PathVariable Long id) {
        Optional<LikesProducto> like = likesRepository.findById(id);
        return like.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // no se ni para que hice este endpoint...
    @GetMapping("/user/{clienteId}")
    @Operation(summary = "Obtener los me gusta de un usuario", description = "Devuelve una lista de me gusta asociados a un usuario específico.")
    public ResponseEntity<?> getLikesByUsuarioId(@PathVariable Long clienteId) {

        List<LikesProducto> likes = likesRepository.findAllByUsuarioId(clienteId);

        if (likes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario sin me gusta");
        }

        return ResponseEntity.ok(likes);
    }

    @GetMapping("/user/productos/{clienteId}")
    @Operation(summary = "Obtiene los productos con me gusta del usuario", description = "Devuelve un DTO con los productos likeados por el usuario")
    public ResponseEntity<?> getLikesByUserId(@PathVariable Long clienteId) {
        List<LikesProductosDTO> likes = likesRepository.findLikesByUserId(clienteId);

        if (likes.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un me gusta", description = "Elimina un me gusta específico por su ID.")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        if (likesRepository.existsById(id)) {
            likesRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/with-likes/{clienteId}")
    @Operation(summary = "Trae tanto productos con o sin Like del usuario", description = "endpoint necesario para mostrar los productos que ya se likearon por el usuario y persistieron en la BD")
    public ResponseEntity<?> getAllwithOrWithoutLike(@PathVariable Long clienteId) {
        return ResponseEntity.ok(likesRepository.findAllWithLikes(clienteId));
    }
}



