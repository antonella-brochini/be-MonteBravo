package com.monteBravo.be.Config.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String calle;
    private String departamento	;
    private String localidad;
    private String manzana	;
    private String numero;
    private String solar;
    private String apartamento;
    private String descripcion;


}