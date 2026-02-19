package com.monteBravo.be.Repository;

import com.monteBravo.be.DTO.LikesProductosDTO;
import com.monteBravo.be.entity.LikesProducto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<LikesProducto, Long> {
    @Query("SELECT l FROM LikesProducto l WHERE l.cliente.idUser = :clienteId AND l.producto.idProducto = :productoId")
    Optional<LikesProducto> findByClienteIdAndProductoId(@Param("clienteId") Long clienteId, @Param("productoId") Long productoId);
    @Query("SELECT l FROM LikesProducto l WHERE l.cliente.idUser = :clienteId")
    List<LikesProducto> findAllByUsuarioId(@Param("clienteId") Long clienteId);
    @Query("SELECT new com.monteBravo.be.DTO.LikesProductosDTO(l.cliente.idUser, l.liked, p.idProducto, p.nombre, p.precio, " +
            "p.descripcion, p.imagen, p.stockActual, p.sku) " +
            "FROM LikesProducto l " +
            "JOIN l.producto p " +
            "WHERE l.cliente.idUser = :clienteId")
    List<LikesProductosDTO> findLikesByUserId(@Param("clienteId") Long clienteId);
    @Query("SELECT new com.monteBravo.be.DTO.LikesProductosDTO(" +
            "CASE WHEN l.liked IS NOT NULL THEN l.liked ELSE false END, " +
            "p.idProducto, p.nombre, p.precio, p.descripcion, p.imagen, p.stockActual, p.sku, c.idCategoriaProducto) " +
            "FROM Producto p " +
            "INNER JOIN CategoriaProducto c ON p.categoriaProducto.idCategoriaProducto = c.idCategoriaProducto " +
            "LEFT JOIN LikesProducto l ON p.idProducto = l.producto.idProducto AND l.cliente.idUser = :clienteId")
    List<LikesProductosDTO> findAllWithLikes(@Param("clienteId") Long clienteId);
    @Query("SELECT COUNT(l) > 0 from LikesProducto l where l.producto.idProducto = :id")
    boolean existeLikeByProducto(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("DELETE FROM LikesProducto lp WHERE lp.producto.idProducto = :idProducto")
    void deleteByProductoId(@Param("idProducto") Long idProducto);
}
