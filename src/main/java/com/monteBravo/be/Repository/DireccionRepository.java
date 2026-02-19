package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

}
