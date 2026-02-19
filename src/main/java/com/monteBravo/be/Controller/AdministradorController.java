package com.monteBravo.be.Controller;

import com.monteBravo.be.Config.response.MessageResponse;
import com.monteBravo.be.DTO.AdministradorDTO;
import com.monteBravo.be.DTO.UsuarioDTO;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Repository.AdministradorRepository;
import com.monteBravo.be.Repository.RoleRepository;
import com.monteBravo.be.Services.UserService;
import com.monteBravo.be.entity.Administrador;
import com.monteBravo.be.entity.AppRole;
import com.monteBravo.be.entity.Role;
import com.monteBravo.be.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/administrador")
@Tag(name = "Administrador", description = "Operaciones relacionadas con los administradores")

public class AdministradorController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    UserService userService;

    @Autowired
    private RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    @Operation(summary = "Crear un administrador", description = "Crea un nuevo administrador en el sistema.")
    @ApiResponse(responseCode = "201", description = "Administrador creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdministradorDTO.class)))
    @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json"))
    public ResponseEntity<AdministradorDTO> crearAdministrador(@RequestBody Administrador administrador) throws Exception {
        if(administradorRepository.findByNombreUsuario(administrador.getNombreUsuario()).isPresent()){
            throw new UsuarioException("El nombre ya esta en uso");
        }
        if (administradorRepository.findByEmail(administrador.getEmail()).isPresent()) {
            throw new UsuarioException("El email ya está en uso.");
        }
        if (administrador.getNombreUsuario().contains("@")) {
            throw new UsuarioException("Error: El nombre de usuario no puede contener el caracter '@'");
        }
        administrador.validate();
        administrador.passwordValidate(administrador.getPassword());
        String hashPassword = passwordEncoder.encode(administrador.getPassword());
        administrador.setPassword(hashPassword);
        Optional<Role> role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN);
        administrador.setRole(role.get());
        administrador.setAccountNonLocked(true);
        administrador.setAccountNonExpired(true);
        administrador.setCredentialsNonExpired(true);
        administrador.setEnabled(true);
        administrador.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        administrador.setAccountExpiryDate(LocalDate.now().plusYears(1));
        administrador.setSignUpMethod("email");
        AdministradorDTO savedAdmin = new AdministradorDTO(administradorRepository.save(administrador));

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }
    @GetMapping
    @Operation(summary = "Listar administradores habilitados", description = "Obtiene la lista de administradores que están habilitados.")
    @ApiResponse(responseCode = "200", description = "Lista de administradores", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AdministradorDTO.class))))
    public List<AdministradorDTO> listarAdministrador() {
        List<Administrador> administradores = administradorRepository.findAllEnabled();
        return administradores.stream().map(AdministradorDTO::new).toList();
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar administrador por ID", description = "Obtiene un administrador por su ID.")
    @ApiResponse(responseCode = "200", description = "Administrador encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdministradorDTO.class)))
    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    public ResponseEntity<AdministradorDTO> buscarAdministrador(@PathVariable Long id) {
        return administradorRepository.findById(id)
                .map(administrador -> ResponseEntity.ok(new AdministradorDTO(administrador)))
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar administrador", description = "Deshabilita a un administrador del sistema.")
    @ApiResponse(responseCode = "204", description = "Administrador deshabilitado exitosamente")
    @ApiResponse(responseCode = "400", description = "Error al eliminar el administrador")

    public ResponseEntity<Void> eliminarAdministrador(@PathVariable Long id) throws Exception {
        //administradorRepository.deleteById(id);
        userService.updateAccountEnabledStatus(id,false);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    @Operation(summary = "Editar administrador", description = "Actualiza los datos de un administrador existente.")
    @ApiResponse(responseCode = "200", description = "Administrador actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdministradorDTO.class)))
    @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    public ResponseEntity<AdministradorDTO> editarAdministrador(@PathVariable Long id, @RequestBody Administrador updatedAdmin) throws Exception {
        Optional<Administrador> admin = administradorRepository.findById(id);
        if (admin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Administrador administrador = admin.get();
        if (!administrador.getEmail().equals(updatedAdmin.getEmail()) && administradorRepository.findByEmail(updatedAdmin.getEmail()).isPresent()) {
            throw new UsuarioException("El email ya está en uso.");
        }
        if (updatedAdmin.getNombreUsuario().contains("@")) {
            throw new UsuarioException("El nombre de usuario no puede contener el caracter '@'");
        }
        administrador.emailValidate(updatedAdmin.getEmail());
        administrador.setEmail(updatedAdmin.getEmail());
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            administrador.passwordValidate(updatedAdmin.getPassword());
            String hashPassword = passwordEncoder.encode(updatedAdmin.getPassword());
            administrador.setPassword(hashPassword);
        }
        AdministradorDTO updatedAdminDTO = new AdministradorDTO(administradorRepository.save(administrador));
        return ResponseEntity.ok(updatedAdminDTO);
    }

    @GetMapping("/deshabilitados")
    @Operation(summary = "Listar usuarios deshabilitados", description = "Obtiene la lista de usuarios deshabilitados (tanto clientes como administradores).")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios deshabilitados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioDTO.class))))
    public List<UsuarioDTO> listarUsuariosDeshabilitados() {
         List<Usuario> usuarios =userService.getAllUsersDisabled();
        return usuarios.stream().map(UsuarioDTO::new).toList();

    }

}

