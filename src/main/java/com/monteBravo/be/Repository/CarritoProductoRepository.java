package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.CarritoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoProductoRepository extends JpaRepository<CarritoProducto, Long> {

    @Query("SELECT cp FROM CarritoProducto cp WHERE cp.carrito.idCarrito = :carritoId AND cp.producto.idProducto = :productoId")
    Optional<CarritoProducto> findCarritoProductoByCarritoIdAndProductoId(
            @Param("carritoId") Long carritoId,
            @Param("productoId") Long productoId
    );

    @Modifying
    @Query("DELETE FROM CarritoProducto cp WHERE cp.carrito.idCarrito = :idCarrito")
    void deleteAllByCarritoId(@Param("idCarrito") Long idCarrito);

}
