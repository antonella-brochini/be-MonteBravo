package com.monteBravo.be.entity;

import com.monteBravo.be.DTO.AdministradorDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario  {
    public Administrador(String nombreUsuario, String email, String password) {
        super(nombreUsuario, email, password);
    }

    public Administrador() {}

    @Override
    public void validate() throws Exception {
        super.validate();
    }
    public  AdministradorDTO convertToDto() {
        return new AdministradorDTO(this);
    }
}
