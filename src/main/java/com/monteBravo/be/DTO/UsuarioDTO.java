package com.monteBravo.be.DTO;

import com.monteBravo.be.entity.Role;
import com.monteBravo.be.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private long idUser;
    private String nombreUsuario;
    private String email;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private String signUpMethod;
    private Role role;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public UsuarioDTO(Usuario usuario) {
        this.idUser = usuario.getIdUser();
        this.nombreUsuario = usuario.getNombreUsuario();
        this.email = usuario.getEmail();
        this.accountNonLocked = usuario.isAccountNonLocked();
        this.accountNonExpired = usuario.isAccountNonExpired();
        this.credentialsNonExpired = usuario.isCredentialsNonExpired();
        this.enabled = usuario.isEnabled();
        this.credentialsExpiryDate = usuario.getCredentialsExpiryDate();
        this.accountExpiryDate = usuario.getAccountExpiryDate();
        this.signUpMethod = usuario.getSignUpMethod();
        this.role = usuario.getRole();
        this.createdDate = usuario.getCreatedDate();
        this.updatedDate = usuario.getUpdatedDate();
    }
}
