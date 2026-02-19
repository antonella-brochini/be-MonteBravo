package com.monteBravo.be.DTO;

import com.monteBravo.be.entity.Administrador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorDTO extends UsuarioDTO {

    private String permisos;
    public AdministradorDTO(Administrador a) {
        super(a.getIdUser(), a.getNombreUsuario(), a.getEmail(),
                a.isAccountNonLocked(), a.isAccountNonExpired(),
                a.isCredentialsNonExpired(), a.isEnabled(),
                a.getCredentialsExpiryDate(), a.getAccountExpiryDate(),
                a.getSignUpMethod(), a.getRole(),
                a.getCreatedDate(), a.getUpdatedDate());
    }

}