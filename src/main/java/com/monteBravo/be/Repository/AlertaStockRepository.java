package com.monteBravo.be.Repository;


import com.monteBravo.be.entity.AlertaStock;
import com.monteBravo.be.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {

    @Query("SELECT a FROM AlertaStock a WHERE a.resuelta = false")
    List<AlertaStock> findNoResuelta();

    @Query("SELECT a FROM AlertaStock a WHERE a.producto = :producto and a.resuelta = false")
    Optional<AlertaStock> findByProducto(@Param("producto") Producto producto);
}
