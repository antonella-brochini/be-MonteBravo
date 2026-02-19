package com.monteBravo.be.Controller;

import com.monteBravo.be.Repository.*;
import com.monteBravo.be.Services.CloudinaryService;
import com.monteBravo.be.entity.CategoriaProducto;
import com.monteBravo.be.entity.Producto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@Tag(name = "Gestión de Productos", description = "Controlador para gestionar los productos del sistema")
@RequestMapping("/api/v1/producto")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final MovStockRepository movStockRepository;
    private final PedidoRepository pedidoRepository;
    private final LikesRepository likesRepository;
    private final OpinionProductoRepository opinionProductoRepository;

    public ProductoController(ProductoRepository productoRepository, CloudinaryService cloudinaryService,
                              CategoriaProductoRepository categoriaProductoRepository, MovStockRepository movStockRepository,
                              PedidoRepository pedidoRepository, LikesRepository likesRepository, OpinionProductoRepository opinionProductoRepository) {
        this.cloudinaryService = cloudinaryService;
        this.productoRepository = productoRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
        this.movStockRepository = movStockRepository;
        this.pedidoRepository = pedidoRepository;
        this.likesRepository = likesRepository;
        this.opinionProductoRepository = opinionProductoRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Permite agregar un nuevo producto proporcionando los datos necesarios")
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        producto.setStockInicial(producto.getStockActual());

        if (productoRepository.existsByNombre(producto.getNombre())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ya existe un producto con el mismo nombre.");
        }

        if (productoRepository.existsBySku(producto.getSku())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ya existe un producto con el mismo SKU.");
        }

        Optional<CategoriaProducto> categoria = categoriaProductoRepository.findById(producto.getCategoriaProducto().getIdCategoriaProducto());
        if (!categoria.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoría especificada no existe.");
        }

        producto.setCategoriaProducto(categoria.get());

        Producto productoGuardado = productoRepository.save(producto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Producto creado correctamente");
        response.put("idProducto", productoGuardado.getIdProducto());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Este endpoint devuelve una lista completa de productos disponibles en el sistema")
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }


    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    public ResponseEntity<Producto> getProductoById(@Parameter(name = "id", description = "ID del producto", in = ParameterIn.PATH, required = true)
                                                    @PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> ResponseEntity.ok(producto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/upload-image")
    @Operation(summary = "Dado un producto se le agrega una imagen", description = "Dado un producto se le agrega una imagen")

    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return productoRepository.findById(id)
                .map(producto -> {
                    try {
                        // Subir la imagen a Cloudinary y obtener la URL
                        Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
                        String imageUrl = (String) uploadResult.get("url");

                        // Asociar la URL de la imagen al producto y guardar
                        producto.setImagen(imageUrl);
                        productoRepository.save(producto);

                        return ResponseEntity.ok(producto);
                    } catch (IOException e) {
                        return ResponseEntity.status(500).body(null);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto por ID")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try{
            Optional<Producto> producto = productoRepository.findById(id);
            if(producto.isPresent()){
                if(!movStockRepository.ExisteMovDeProducto(id).isEmpty() && producto.get().getStockActual() > 0){
                    return ResponseEntity.status(409).body("No se pudo eliminar el producto porque tiene Stock asociado, primero dar de baja el stock en Panel STOCK");
                }
                if(pedidoRepository.existsPedidoByProductoId(id)){
                    return ResponseEntity.status(409).body("El producto " + producto.get().getNombre() + " no puede ser eliminado ya que hay pedidos asociados");
                }
                if(likesRepository.existeLikeByProducto(id)){
                    likesRepository.deleteByProductoId(id);
                }
                if(opinionProductoRepository.existeOpinionByProductoId(id)){
                    opinionProductoRepository.deleteByProductoId(id);
                }
                productoRepository.deleteById(id);
                return ResponseEntity.status(204).body("Eliminado correctamente");
            }else{
                return ResponseEntity.status(404).body("No se encontro producto con Id = " + id);
            }
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error interno, comuniquese con el administrador del sistema");
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Edita un producto", description = "Edita un producto por ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestBody Producto updatedProduct) {
        try {

            Optional<Producto> existingProductOptional = productoRepository.findById(id);

            if (existingProductOptional.isPresent()) {
                Producto existingProduct = existingProductOptional.get();

                existingProduct.setNombre(updatedProduct.getNombre());
                existingProduct.setDescripcion(updatedProduct.getDescripcion());
                existingProduct.setPrecio(updatedProduct.getPrecio());
                existingProduct.setStockActual(updatedProduct.getStockActual());
                existingProduct.setStockInicial(updatedProduct.getStockActual());
                existingProduct.setSku(updatedProduct.getSku());

                if (updatedProduct.getCategoriaProducto() != null) {
                    Optional<CategoriaProducto> cp =  categoriaProductoRepository.findById(updatedProduct.getCategoriaProducto().getIdCategoriaProducto());
                    if(cp.isPresent()){
                        existingProduct.setCategoriaProducto(updatedProduct.getCategoriaProducto());
                    }else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria seleccionada no existe");
                    }
                }
                productoRepository.save(existingProduct);
                return ResponseEntity.ok("Producto actualizado correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el producto");
        }
    }

}
