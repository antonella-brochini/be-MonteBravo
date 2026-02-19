package com.monteBravo.be.Repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.monteBravo.be.entity.Administrador;
import com.monteBravo.be.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c JOIN Usuario u ON c.idUser = u.idUser WHERE c.nombreUsuario = :nombreUsuario")
    Optional<Cliente> findByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);


    @Query("SELECT c FROM Cliente c WHERE c.idUser IN :ids")
    List<Cliente> findByIds(@Param("ids") List<Long> ids);


    @Query("SELECT c FROM Cliente c JOIN Usuario u ON c.idUser = u.idUser WHERE c.email = :email")
    Optional<Cliente> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM Cliente c JOIN Usuario u ON c.idUser = u.idUser WHERE c.enabled=true and  c.nombreUsuario = :nombreUsuario")
    Optional<Cliente> findByNombreUsuarioActivo(@Param("nombreUsuario") String nombreUsuario);
    @Query("SELECT c FROM Cliente c WHERE c.enabled=false")
    List<Cliente> findAllDisabled();
    @Query("SELECT c FROM Cliente c WHERE c.enabled=true")
    List<Cliente> findAllEnabled();

    @Query("SELECT c FROM Cliente c WHERE c.idUser=:idUser")
    Optional<Cliente> findByIdUser(@Param("idUser") Long idUser);

}

