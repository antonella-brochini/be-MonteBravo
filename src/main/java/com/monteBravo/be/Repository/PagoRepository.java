package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.OpinionProducto;
import com.monteBravo.be.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("SELECT p FROM Pago p WHERE p.idMercadoPago = :idMercadoPago")
    Optional<Pago> findByIdMercadoPago(@Param("idMercadoPago") Long idMercadoPago);




}
