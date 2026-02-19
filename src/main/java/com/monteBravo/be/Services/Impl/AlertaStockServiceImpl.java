package com.monteBravo.be.Services.Impl;

import com.monteBravo.be.Repository.AdministradorRepository;
import com.monteBravo.be.Repository.AlertaStockRepository;
import com.monteBravo.be.Services.SendEmail;
import com.monteBravo.be.entity.Administrador;
import com.monteBravo.be.entity.AlertaStock;
import com.monteBravo.be.entity.Producto;
import com.monteBravo.be.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlertaStockServiceImpl {
    @Autowired
    private AlertaStockRepository alertaStockRepository;
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    SendEmail sendEmail;



    public void verificarYGenerarAlerta(Producto producto) {
        boolean verificarAlerta = producto.esStockBajo();
        Optional<AlertaStock> alertaStock = alertaStockRepository.findByProducto(producto);
        producto.setDeshabilitadoPorStock(producto.getStockActual() == 0);
        if(alertaStock.isEmpty() && verificarAlerta) {
            AlertaStock alerta = new AlertaStock(producto, "Bajo Stock");
            alertaStockRepository.save(alerta);
        }
        if(alertaStock.isPresent() && !verificarAlerta) {
            AlertaStock alerta = alertaStock.get();
            alerta.setResuelta(true);
            alertaStockRepository.save(alerta);
        }
    }

    public void enviarMailAlerta(Producto producto) {

        List<Administrador> administradores = administradorRepository.findAll();
        List<Usuario> usuarios = new ArrayList<>(administradores);
        List<Map<String,String>> variables = new ArrayList<>();
        Map<String,String> url = new HashMap<>();
        Map<String,String> name = new HashMap<>();
        url.put("variable", "producto");
        url.put("content", producto.getNombre());
        name.put("variable", "nombreUsuario");
        name.put("content", "Holi");
        variables.add(url);
        variables.add(name);
        //sendEmail.sendEmailBatch(usuarios,variables,"d-0202e12c7eee417ba00a681471773e4b" );
    }
}
