package com.monteBravo.be.Repository;

import com.monteBravo.be.DTO.PedidoProductoDTO;
import com.monteBravo.be.entity.PedidoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Long> {
    @Query("SELECT new com.monteBravo.be.DTO.PedidoProductoDTO(" +
            "pp.producto.nombre, " +
            "pp.cantidad, " +
            "pp.precio, " +
            "(pp.cantidad * pp.precio), " +
            "pp.producto.imagen, " +
            "pp.producto.idProducto) " +
            "FROM PedidoProducto pp " +
            "WHERE pp.pedido.idPedido = :idPedido")
    List<PedidoProductoDTO> findPedidoProductosByPedidoId(@Param("idPedido") Long idPedido);
}
