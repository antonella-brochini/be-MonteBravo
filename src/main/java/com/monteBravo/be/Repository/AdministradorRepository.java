package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    @Query("SELECT a FROM Administrador a JOIN Usuario u ON a.idUser = u.idUser WHERE a.nombreUsuario = :nombreUsuario")
    Optional<Administrador> findByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);


    @Query("SELECT a FROM Administrador a JOIN Usuario u ON a.idUser = u.idUser WHERE a.email = :email")
    Optional<Administrador> findByEmail(@Param("email") String email);

    @Query("SELECT a FROM Administrador a JOIN Usuario u ON a.idUser = u.idUser WHERE a.enabled=true and a.nombreUsuario = :nombreUsuario")
    Optional<Administrador> findByNombreUsuarioActivo(@Param("nombreUsuario") String nombreUsuario);
    @Query("SELECT a FROM Administrador a WHERE a.enabled=false")
    List<Administrador> findAllDisabled();
    @Query("SELECT a FROM Administrador a WHERE a.enabled=true")
    List<Administrador> findAllEnabled();

}
