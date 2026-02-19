package com.monteBravo.be.DTO;

public class PreferenciaDTO {
    private EnvioDTO envio;
    private CarritoDTO carrito;

    public EnvioDTO getEnvio() {
        return envio;
    }
    public void setEnvio(EnvioDTO envio) {
        this.envio = envio;

    }

    public CarritoDTO getCarrito() {
        return carrito;
    }
    public void setCarrito(CarritoDTO carrito) {
        this.carrito = carrito;
    }

    public PreferenciaDTO(EnvioDTO envio, CarritoDTO carrito) {
        this.envio = envio;
        this.carrito = carrito;
    }
    public PreferenciaDTO() {}
}
