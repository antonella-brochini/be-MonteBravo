package com.monteBravo.be.Controller;

import com.monteBravo.be.DTO.AdministradorDTO;
import com.monteBravo.be.DTO.ClienteDTO;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Repository.CarritoRepository;
import com.monteBravo.be.entity.Administrador;
import com.monteBravo.be.entity.Carrito;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;


@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {


    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("all-clientes")
    public List<ClienteDTO> listarClientes(){
        List<Cliente> clientes = clienteRepository.findAllEnabled();
        return clientes.stream().map(ClienteDTO::new).toList();

    }



    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarCliente(@PathVariable Long id){
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok(new ClienteDTO(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }
}
