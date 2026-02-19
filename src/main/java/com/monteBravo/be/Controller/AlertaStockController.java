package com.monteBravo.be.Controller;

import com.monteBravo.be.Repository.AlertaStockRepository;
import com.monteBravo.be.entity.AlertaStock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@Tag(name = "Gestión de Alertas de stock", description = "Controlador para gestionar las alertas de stock de productos del sistema")
@RequestMapping("/api/v1/alerta-stock")
public class AlertaStockController {
    @Autowired
    private AlertaStockRepository alertaStockRepository;

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @GetMapping
        @Operation(summary = "Obtener todas las alertas no resueltas", description = "Este endpoint devuelve una lista de alertas de bajo stock de productos en el sistema")
        public List<AlertaStock> getAllAlertas() {
            return alertaStockRepository.findNoResuelta();
        }


        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @GetMapping("/{id}")
        @Operation(summary = "Obtener una alerta por ID")
        public ResponseEntity<AlertaStock> getAlertaById(@Parameter(name = "id", description = "ID de la alerta", in = ParameterIn.PATH, required = true)
                                                        @PathVariable Long id) {
            return alertaStockRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PutMapping("/{id}")
        @Operation(summary = "Edita una alerta", description = "Edita una alerta por ID")

        public ResponseEntity<?> editAlerta(@PathVariable Long id) {
            try {

                    Optional<AlertaStock> existingAlerta = alertaStockRepository.findById(id);

                if (existingAlerta.isPresent()) {
                    AlertaStock alerta = existingAlerta.get();
                    alerta.setResuelta(true);
                    alertaStockRepository.save(alerta);
                    return ResponseEntity.ok("Alerta actualizada correctamente");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alerta no encontrado");
                }
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la alerta");
            }
        }

}
