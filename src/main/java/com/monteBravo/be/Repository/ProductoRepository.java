package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombre(String nombre);
    boolean existsBySku(String sku);
    @Query("SELECT p FROM Producto p WHERE p.stockActual > 0")
    List<Producto> getProductoConStock();
    @Query("SELECT p FROM Producto p WHERE p.categoriaProducto.idCategoriaProducto = :id")
    List<Producto> getProductoPorCategoria(@Param("id") Long id);
    @Query("SELECT p FROM Producto p WHERE p.stockActual >= :stockMayorA AND p.stockActual <= :stockMenorA")
    List<Producto> findProductosByStockRange(@Param("stockMayorA") int stockMayorA, @Param("stockMenorA") int stockMenorA);


}
