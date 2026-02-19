package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.PedidoDTO;
import com.monteBravo.be.DTO.PedidoDetalleDTO;
import com.monteBravo.be.DTO.PedidoProductoDTO;
import com.monteBravo.be.Repository.ClienteRepository;
import com.monteBravo.be.Repository.PedidoProductoRepository;
import com.monteBravo.be.Repository.PedidoRepository;
import com.monteBravo.be.Services.PedidoService;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.Pedido;
import com.monteBravo.be.entity.PedidoProducto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pedido")
public class PedidoController {

  private PedidoService pedidoService;


    // Inyecto las dependencias (se puede hacer con @Autowired tmb)
    public PedidoController(PedidoService pedidoService) {
     this.pedidoService= pedidoService;
    }

    @Operation(summary = "Crea un pedido", description = "Crea un pedido y sus pedido productos que son el detalle del pedido, como los productos, cantidades y precio ")
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {

        pedidoService.crearPedido(pedido);
        return pedido;
    }

    @Operation(summary = "Get all opiniones pedidos", description = "Devuelve todos los pedidos de todos los usuarios")
    @GetMapping
    public List<Pedido> getAllPedido() {
        return pedidoService.findAllPedidos();
    }
    @Operation(summary = "Get pedido por ID", description = "Devuelve un pedido en especifico")
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        Optional<Pedido> pedido =  pedidoService.findById(id);
        return pedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Operation(summary = "Get detalle pedidos", description = "Devuelve todos los pedidos y su detalle, usado para mostrar el historial de pedidos del usuario")
    @GetMapping("/detalle/{idUser}")
    public ResponseEntity<?> getPedidosDetalle(@PathVariable Long idUser) {
        try {
            List<PedidoDetalleDTO> detalles = getPedidosDetallePorUsuario(idUser);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los pedidos: " + e.getMessage());
        }
    }

    public List<PedidoDetalleDTO> getPedidosDetallePorUsuario(Long idUser) {
        List<Pedido> pedidos = pedidoService.findByClientId(idUser);
        return pedidos.stream()
                .map(pedido -> {
                    List<PedidoProductoDTO> productosDTO = pedidoService.findPedidoProductoByPedidoId(pedido.getIdPedido());

                    double total = productosDTO.stream()
                            .mapToDouble(PedidoProductoDTO::getPrecioTotal)
                            .sum();

                    PedidoDTO pedidoDTO = new PedidoDTO(
                            pedido.getIdPedido(),
                            pedido.getFechaCompra(),
                            total
                    );
                    return new PedidoDetalleDTO(pedidoDTO, productosDTO);
                })
                .toList();
    }

    @GetMapping("/decendente")
    public List<Pedido> todosDecendente(){

        return pedidoService.todosDecendente();
    }

}
