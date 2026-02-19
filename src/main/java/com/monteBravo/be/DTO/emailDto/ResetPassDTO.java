package com.monteBravo.be.DTO.emailDto;

import com.monteBravo.be.Inteface.IResolvedorVariable;

public class ResetPassDTO implements IResolvedorVariable{

    private String nombreUsuario;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public ResetPassDTO(String nombreUsuario, String url) {
        this.nombreUsuario = nombreUsuario;
        this.url = url;
    }

    @Override
    public String devolverGetterPorNombre(String nombreVariable) {
        switch (nombreVariable.toLowerCase()){
            case "url":
                return url;
            case "nombreusuario":
                    return nombreUsuario;
            default:
                return "";
        }

    }
}
