package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovStockRepository extends JpaRepository<MovimientoStock, Long> {

    @Query("SELECT m FROM MovimientoStock m ORDER BY m.fecha DESC")
    List<MovimientoStock> findAllOrderByFechaDesc();

    @Query("SELECT m FROM MovimientoStock m WHERE m.producto.idProducto = :id")
    List<MovimientoStock> ExisteMovDeProducto(@Param("id") Long id);
    // esto no me cierra, deberia dejar eliminar si el stock del prod esta en 0
    // xq sino, el usuario deberia borrar todos los movStock que referencien al id..



}
