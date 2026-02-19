package com.monteBravo.be.DTO;

import com.monteBravo.be.entity.Carrito;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.OpinionProducto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO extends UsuarioDTO {
    private String direccion;
    private List<OpinionProducto> opiniones;
    private Carrito carrito;
    private String calle;
    private String departamento	;
    private String localidad;
    private String manzana	;
    private String numero;
    private String solar;
    private String apartamento;
    private String descripcion;

    public ClienteDTO(Cliente c) {
        super(c.getIdUser(), c.getNombreUsuario(), c.getEmail(),
                c.isAccountNonLocked(), c.isAccountNonExpired(),
                c.isCredentialsNonExpired(), c.isEnabled(),
                c.getCredentialsExpiryDate(), c.getAccountExpiryDate(),
                c.getSignUpMethod(), c.getRole(),
                c.getCreatedDate(), c.getUpdatedDate());
        this.opiniones = c.getOpiniones();
        this.carrito = c.getCarrito();
if(c.getDireccion()!=null){
    this.calle= c.getDireccion().getCalle();
    this.departamento = c.getDireccion().getDepartamento()	;
    this.localidad = c.getDireccion().getLocalidad();
    this.manzana= c.getDireccion().getManzana()	;
    this.numero= c.getDireccion().getNumero();
    this.solar= c.getDireccion().getSolar();
    this.apartamento = c.getDireccion().getApartamento();
    this.descripcion = c.getDireccion().getDescripcion();


}

    }
}