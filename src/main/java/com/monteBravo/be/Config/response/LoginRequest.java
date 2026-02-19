package com.monteBravo.be.Config.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String nombreUsuario;
    private String password;
}