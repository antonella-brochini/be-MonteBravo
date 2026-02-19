package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.ItemCarritoDTO;
import com.monteBravo.be.Repository.CarritoProductoRepository;
import com.monteBravo.be.Repository.CarritoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.Services.CarritoProductoService;
import com.monteBravo.be.entity.Carrito;
import com.monteBravo.be.entity.CarritoProducto;
import com.monteBravo.be.entity.Producto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/carritoProducto")
public class CarritoProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoProductoService carritoProductoService;

    @PostMapping("/agregarCarritoProducto/{idCarrito}")
    @Operation(
            summary = "Agregar un nuevo producto en el carrito",
            description = "Permite agregar un nuevo producto al carrito. Se requiere el identificador del producto, la cantidad deseada, el precio y id del carrito. La operación devuelve el carritoPoducto agregado al carrito. Si ocurre un error, se devuelve un mensaje con detalles sobre la causa del problema."
    )
    public ResponseEntity<?> agregarCarritoProducto(@Valid @RequestBody ItemCarritoDTO CarritoProducto ,  @PathVariable long idCarrito){

        Carrito carrito = carritoRepository.findById(idCarrito).orElse(null);
        if(carrito == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Carrito.");

        Producto producto = productoRepository.findById(CarritoProducto.getIdProducto()).orElse(null);
        if(producto == null){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Producto.");}
        // controlo que no exista este carrito producto
        Optional<CarritoProducto> carritoProducto = carritoProductoService.findByIdProductoAndCarritoId(producto.getIdProducto(),carrito.getIdCarrito());
        if(carritoProducto.isEmpty()){
            CarritoProducto nuevoCarritoProducto = new CarritoProducto();
            nuevoCarritoProducto.setCarrito(carrito);
            nuevoCarritoProducto.setProducto(producto);
            nuevoCarritoProducto.setCantidad(CarritoProducto.getCantidad());
            nuevoCarritoProducto.setPrecio(producto.getPrecio());
            carritoProductoService.guardar(nuevoCarritoProducto);
            carrito.setUltimaInteraccion(new Date());
            carritoRepository.save(carrito);
            carrito.setUltimaInteraccion(new Date());
            carritoRepository.save(carrito); // Actualizo fecha ultima interaccion
            return ResponseEntity.ok(nuevoCarritoProducto);
        }


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este producto ya existe en el carrito.");
    }


    @PutMapping("/editarCarritoProducto/{idCarrito}")
    @Operation(
            summary = "Edita la cantidad de un producto y lo agrega al carrito ",
            description = "Permite editar la cantidad deseada de un producto para agregarlo al carrito. Se requiere el identificador del producto, la cantidad deseada y id del carrito. La operación devuelve el carritoPoducto agregado al carrito. Si ocurre un error, se devuelve un mensaje con detalles sobre la causa del problema."
    )
    public ResponseEntity<?> editarCarritoProducto(@Valid @RequestBody ItemCarritoDTO CarritoProducto , @PathVariable long idCarrito){
        Carrito carrito = carritoRepository.findById(idCarrito).orElse(null);
        if(carrito == null){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Carrito.");}

        Producto producto = productoRepository.findById(CarritoProducto.getIdProducto()).orElse(null);
        if(producto == null){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Producto.");}

        //busco carrito producto
        Optional<CarritoProducto> carritoProducto = carritoProductoService.findByIdProductoAndCarritoId(producto.getIdProducto(),carrito.getIdCarrito());
        if(!carritoProducto.isEmpty()){
            carritoProducto.get().setCantidad(CarritoProducto.getCantidad());
            carritoProductoService.guardar(carritoProducto.get());
            carrito.setUltimaInteraccion(new Date());
             carritoRepository.save(carrito);
            carrito.setUltimaInteraccion(new Date());
            carritoRepository.save(carrito); // Actualizo fecha ultima interaccion
            return ResponseEntity.ok(carritoProducto.get());
        }
         else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe este producto en este carrito.");

    }


    @DeleteMapping("/{idCarrito}/{idProducto}")
    @Operation(
            summary = "Eliminar un producto del carrito",
            description = "Permite eliminar un producto específico del carrito de compras. Se requiere el identificador único del producto para proceder con la eliminación, y el id del carrito. La operación devuelve el estado actualizado del carrito después de la eliminación. Si el producto no se encuentra en el carrito o si ocurre un error, se devuelve un mensaje de error detallado."
    )
    public ResponseEntity<?> borrarCarritoProducto(@Valid @PathVariable long idCarrito, @Valid @PathVariable long idProducto){
        Carrito carrito = carritoRepository.findById(idCarrito).orElse(null);
        if(carrito == null){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Carrito.");}

        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if(producto == null){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe Producto.");}

        //busco carrito producto
        Optional<CarritoProducto> carritoProducto = carritoProductoService.findByIdProductoAndCarritoId(producto.getIdProducto(),carrito.getIdCarrito());
        if(!carritoProducto.isEmpty()){
            carritoProductoService.borrar(carritoProducto.get());
            carrito.setUltimaInteraccion(new Date());
            carritoRepository.save(carrito);
            carrito.setUltimaInteraccion(new Date());
            carritoRepository.save(carrito); // Actualizo fecha ultima interaccion
            return ResponseEntity.noContent().build();
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No existe este producto en este carrito.");

    }


    @GetMapping("/todos")
    public List<CarritoProducto> getAllCarritoProducto(){
        return carritoProductoService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un carritoProducto del carrito por ID de producto y carrito",
            description = "Permite obtener un producto específico de un carrito de compras utilizando tanto el identificador único del producto como el identificador único del carrito. La operación devuelve los detalles del producto dentro del carrito, incluyendo la cantidad, el precio y cualquier otra información relevante. Si no se encuentra el producto en el carrito con los IDs proporcionados, se devuelve un mensaje de error."
    )
    public ResponseEntity<CarritoProducto> getCarritoProductoPorId(@PathVariable Long id){
        Optional<CarritoProducto> cp = carritoProductoService.findById(id);
        return cp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().body(null));
    }
}
