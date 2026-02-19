package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("SELECT p FROM Pedido p WHERE p.cliente.idUser = :idUser")
    List<Pedido> findAllByClienteId(@Param("idUser") Long idUser);



    @Query("SELECT p FROM Pedido p ORDER BY p.fechaCompra DESC")
    List<Pedido> findAllDecendente();

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
            "FROM pedido p " +
            "INNER JOIN ped_productos pp ON p.id_pedido = pp.id_pedido " +
            "WHERE pp.id_producto = :idProducto",
            nativeQuery = true)
    boolean existsPedidoByProductoId(@Param("idProducto") Long idProducto);

}
