package com.monteBravo.be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.monteBravo.be.DTO.ClienteDTO;
import com.monteBravo.be.DTO.UsuarioDTO;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Inteface.IValidable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario implements IValidable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;
    @Column(unique = true)
    private String email;
    private String password;

    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private String signUpMethod;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonBackReference
    @ToString.Exclude
    private Role role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;


    public Usuario(String nombreUsuario, String email, String password) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
    }
    public Usuario(String userName, String email) {
        this.nombreUsuario = userName;
        this.email = email;
    }

    public Usuario(){}

    @Override
    public void validate() throws Exception {
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            throw new UsuarioException("El nombre no puede estar vacío.");
        }
        if (email == null || email.isEmpty()) {
            throw new UsuarioException("El email no puede estar vacío.");
        }
        if(signUpMethod == "email"){
            if (password == null || password.isEmpty()) {
                throw new UsuarioException("La contraseña no puede estar vacía.");
            }
        }

        emailValidate(email);

    }

    public void emailValidate(String email) throws UsuarioException {
         String regex ="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new UsuarioException("El email debe tener un formato valido");
        }
    }

    public void passwordValidate(String password) throws UsuarioException {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[.,#;:!@$%&*_-]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            throw new UsuarioException("La contraseña debe contener al menos 8 caracteres, una mayuscula, una minuscula y un caracter especial.");
        }
    }
    public abstract UsuarioDTO convertToDto();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return idUser == usuario.idUser;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
