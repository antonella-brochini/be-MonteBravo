package com.monteBravo.be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monteBravo.be.DTO.AdministradorDTO;
import com.monteBravo.be.DTO.ClienteDTO;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Inteface.IResolvedorVariable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.*;


import java.util.List;
import java.util.function.Function;

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario implements IResolvedorVariable {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id_direcion")
    private Direccion direccion;
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<OpinionProducto> opiniones;
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Carrito carrito;

    public Cliente(String nombreUsuario, String email, String password, Direccion direccion, List<OpinionProducto> opiniones, Carrito carrito) {
        super(nombreUsuario, email, password);
        this.direccion = direccion;
        this.opiniones = opiniones;
        this.carrito = carrito;
    }

    public Cliente(String nombreUsuario, String email, String password) {
        super(nombreUsuario, email, password);
    }

    public Cliente() {}

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public List<OpinionProducto> getOpiniones() {
        return opiniones;
    }

    public void setOpiniones(List<OpinionProducto> opiniones) {
        this.opiniones = opiniones;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }


    public static class AtributoHandler {
        // Método que devuelve el getter dinámico
       public static Function<Cliente, String> checkear(String value ) {

             switch (value.toLowerCase()) {
                case "nombreusuario":
                return Cliente::getNombreUsuario;

            }
            return null; // Devuelve null si no se encuentra coincidencia
       }
    }


    @Override
    public String devolverGetterPorNombre(String nombreProp){
        switch(nombreProp.toLowerCase()){
            case "nombreusuario": return this.getNombreUsuario();
            default: return "";
        }
    }

    @Override
    public void validate() throws Exception {
        super.validate();
    }
    public ClienteDTO convertToDto() {
        return new ClienteDTO(this);
    }
}
