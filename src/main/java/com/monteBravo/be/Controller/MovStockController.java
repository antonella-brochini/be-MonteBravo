package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.StockDTO;
import com.monteBravo.be.Services.MovStockService;
import com.monteBravo.be.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;


@RestController
@Tag(name = "Gestion movimiento de stock", description = "Controlador para gestionar los movimientos de stock de los productos del sistema")
@RequestMapping("api/v1/movstok")
public class MovStockController {

    @Autowired
    private MovStockService movStockService;


    // los endpoint por lo general no deben ser nombre de acciones.. crear, eliminar, etc.
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("crear_mov")
    @Operation(summary = "Crea un movimiento de stock para un producto seleccionado", description = "Genera un movimiento de Stock +/-")
    public ResponseEntity<?> crearMovimiento(@Valid @RequestBody StockDTO stock, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                // Obtén solo el mensaje predeterminado
                errors.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        try{
            MovimientoStock mov = movStockService.crear(stock);
            return ResponseEntity.ok().body(mov);
        }catch(BadRequestException ex){
            List<String> errors = new ArrayList<>();
            errors.add(ex.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/listar_movimientos")
    public List<MovimientoStock> listarMovimientos() {

        return movStockService.todos();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar movimiento de stock por ID",
            description = "Permite obtener los detalles de un movimiento de stock específico mediante su identificador único. La operación devuelve la información completa del movimiento de stock,incluyendo el nombre del producto, la cantidad, el signo,el adminitrador que lo creo, la fecha y el motivo Si no se encuentra un movimiento con el ID proporcionado, se devuelve un mensaje de error."
    )

    public ResponseEntity<MovimientoStock> buscarMovimiento(@PathVariable Long id) {
        MovimientoStock m = movStockService.getById(id);
        if(m==null)
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        else
            return ResponseEntity.ok(m);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("borrar/{id}")
    @Operation(
            summary = "Eliminar un movimiento de stock",
            description = "Permite eliminar un movimiento de stock previamente registrado en el sistema. Se requiere el identificador único del movimiento de stock a eliminar. Si la operación es exitosa, el movimiento de stock se elimina de la base de datos. En caso de error (por ejemplo, si el movimiento no existe), se devuelve un mensaje de error con detalles sobre la falla."
    )
    public ResponseEntity<Void> borrarMovimiento(@PathVariable Long id) {
        try{
            movStockService.borrar(id);
            return ResponseEntity.noContent().build();
        }catch(BadRequestException e){
            return ResponseEntity.notFound().build();

        }

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("editar/{id}")
    @Operation(
            summary = "Editar un movimiento de stock",
            description = "Permite modificar un movimiento de stock previamente registrado en el sistema. Se requiere el identificador único del movimiento de stock y los nuevos valores para los campos de cantidad, signo, y motivo. La operación devuelve el movimiento de stock actualizado o un mensaje de error si los datos proporcionados no son válidos o si el movimiento no existe."
    )

    public ResponseEntity<?> modificarProducto(@PathVariable Long id, @Valid @RequestBody StockDTO movActualizadoDTO,BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                // Obtén solo el mensaje predeterminado
                errors.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        if (id == null) {
        return ResponseEntity.badRequest().body("is stock nulo");
    }
    try{
        MovimientoStock mov =  movStockService.editar(id, movActualizadoDTO);

       return ResponseEntity.ok(mov);
    }
    catch(BadRequestException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/fecha_Decendente")
    @Operation(
            summary = "Listar movimientos de stock por fecha descendente",
            description = "Permite obtener todos los movimientos de stock registrados en el sistema, ordenados por fecha en orden descendente. Los movimientos se devuelven con los detalles completos, incluyendo el nombre del producto, la cantidad, el motivo de movimiento, la fecha y el adminitrador que creo cada movimiento de stock. Esta operación permite realizar consultas eficientes para revisar los movimientos más recientes."
    )
    public List<MovimientoStock> obtenerMovimientosDecendente() {

        return movStockService.decendente();
    }
}
