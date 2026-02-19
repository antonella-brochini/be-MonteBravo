package com.monteBravo.be.Controller;

import com.monteBravo.be.Repository.CategoriaProductoRepository;
import com.monteBravo.be.Repository.ProductoRepository;
import com.monteBravo.be.entity.CategoriaProducto;
import com.monteBravo.be.entity.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categoriaProducto")
public class CategoriaProductoContoller {

    private CategoriaProductoRepository categoriaProductoRepository;
    private ProductoRepository productoRepository;

    public CategoriaProductoContoller(CategoriaProductoRepository categoriaProductoRepository, ProductoRepository productoRepository){
        this.categoriaProductoRepository = categoriaProductoRepository;
        this.productoRepository = productoRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearCategoriaProducto(@RequestBody CategoriaProducto categoriaProducto) {
        Optional<CategoriaProducto> existingCategoria = categoriaProductoRepository.findByNombre(categoriaProducto.getNombre());
        if(existingCategoria.isPresent()){
            return ResponseEntity.badRequest().body("La categoria con el nombre: " + categoriaProducto.getNombre() + " ya existe");
        }

        CategoriaProducto nuevaCategoria = categoriaProductoRepository.save(categoriaProducto);
        return ResponseEntity.ok(nuevaCategoria);
    }

    @GetMapping
    public List<CategoriaProducto> listarCategoriaProducto() {
        return categoriaProductoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProducto> getProductoById(@PathVariable Long id) {
        return categoriaProductoRepository.findById(id)
                .map(CategoriaProducto -> ResponseEntity.ok(CategoriaProducto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarCategoriaProducto(@RequestBody CategoriaProducto categoriaProducto, @PathVariable Long id) {
        Optional<CategoriaProducto> existingCategoria = categoriaProductoRepository.findById(id);
        if(existingCategoria.isPresent()){
            existingCategoria.get().setDescripcion(categoriaProducto.getDescripcion());
            categoriaProductoRepository.save(existingCategoria.get());
            return ResponseEntity.ok(existingCategoria.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoriaProducto(@PathVariable Long id) {
        if(id == 1 || id == 2){
            return ResponseEntity.badRequest().body("No se puede eliminar la categoria ya que se encuentra como categoria destacada en la pagina principal");
        }
        Optional<CategoriaProducto> existingCategoria = categoriaProductoRepository.findById(id);
        if(existingCategoria.isPresent()){

            List<Producto> productoDeLaCategoria = productoRepository.getProductoPorCategoria(id);
            if(productoDeLaCategoria.isEmpty()){
                categoriaProductoRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
        }else{
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().body("No se puede eliminar la categoria ya que tiene productos asociados");
    }



}
