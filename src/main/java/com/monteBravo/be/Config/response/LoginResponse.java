package com.monteBravo.be.Config.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LoginResponse {
    private String jwtToken;
    private String nombreUsuario;
    private List<String> roles;
    private long id;

    public LoginResponse(String nombreUsuario, List<String> roles, String jwtToken, Long id) {
        this.nombreUsuario = nombreUsuario;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.id = id;
    }

}
