package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.OpinionProducto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpinionProductoRepository extends JpaRepository<OpinionProducto, Long> {
    @Query("SELECT p FROM OpinionProducto p WHERE p.producto.idProducto = :idProducto")
    List<OpinionProducto> findByProductoId(@Param("idProducto") Long idProducto);
    @Query("SELECT COUNT(P) > 0 from OpinionProducto P where P.producto.idProducto = :id")
    boolean existeOpinionByProductoId(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("DELETE FROM OpinionProducto P WHERE P.producto.idProducto = :idProducto")
    void deleteByProductoId(@Param("idProducto") Long idProducto);
}
