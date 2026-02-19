package com.monteBravo.be.Controller;

import com.monteBravo.be.Config.JWTConfig.JwtUtils;
import com.monteBravo.be.Config.request.*;
import com.monteBravo.be.Config.response.EditRequest;
import com.monteBravo.be.Config.response.LoginResponse;
import com.monteBravo.be.Config.response.MessageResponse;
import com.monteBravo.be.DTO.AdministradorDTO;
import com.monteBravo.be.DTO.UsuarioDTO;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Repository.*;
import com.monteBravo.be.Services.Impl.UserDetailsImpl;
import com.monteBravo.be.Services.UserService;
import com.monteBravo.be.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthController", description = "Controlador para la autenticación y gestión de usuarios")
public class AuthController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AdministradorRepository administradorRepository;

    @Autowired
    DireccionRepository direccionRepository;
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;
    @Autowired
    private CarritoRepository carritoRepository;


    @PostMapping("/public/signin")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y genera un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, devuelve el token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getNombreUsuario(), loginRequest.getPassword());
            authentication=authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", exception.getMessage());
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

//      Set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(),
                roles, jwtToken, userDetails.getId());

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }


    @PutMapping("/user/edit")
    @Operation(summary = "Editar usuario", description = "Editar la información de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario editado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la validación o datos incorrectos")
    })
    public ResponseEntity<?> editUser(@Valid @RequestBody EditRequest editRequest) throws Exception {

        Optional<Cliente> usuario = clienteRepository.findByNombreUsuario(editRequest.getNombreUsuario());
        Cliente user= usuario.get();

        if (!user.getEmail().equals(editRequest.getEmail()) &&(clienteRepository.findByEmail(editRequest.getEmail()).isPresent()||administradorRepository.findByEmail(editRequest.getEmail()).isPresent())) {
            throw new UsuarioException("Email ya esta en uso");
        }
        if(!user.getEmail().isEmpty()){
            user.setEmail(editRequest.getEmail());
        }
        if(!editRequest.getCalle().isEmpty()){
            Direccion direccion = new Direccion(editRequest.getDepartamento(), editRequest.getLocalidad(),
                    editRequest.getManzana(), editRequest.getNumero(), editRequest.getSolar(), editRequest.getApartamento(), editRequest.getDescripcion(), editRequest.getCalle());
            if(!direccion.equals(user.getDireccion())) {
                direccion.validate();
                direccionRepository.save(direccion);
                user.setDireccion(direccion);
            }
        }
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setSignUpMethod("email");

        try {
            user.validate();
            clienteRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("¡Usuario actualizado con éxito!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }

    @PostMapping("/public/signup")
    @Operation(summary = "Registrar usuario", description = "Registrar un nuevo usuario cliente  en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la validación")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws Exception {
        if (clienteRepository.findByNombreUsuario(signUpRequest.getUsername()).isPresent() || administradorRepository.findByNombreUsuario(signUpRequest.getUsername()).isPresent()) {
            throw new UsuarioException( "Nombre de usuario ya esta en uso.");
        }

        if (clienteRepository.findByEmail(signUpRequest.getEmail()).isPresent()||administradorRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UsuarioException("Email ya esta en uso");
        }
        if (signUpRequest.getUsername().contains("@")) {
            throw new UsuarioException("El nombre de usuario no puede contener el caracter '@'");
        }
        Cliente user = new Cliente(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());


        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        if(!signUpRequest.getCalle().isEmpty()){
            Direccion direccion = new Direccion(signUpRequest.getDepartamento(), signUpRequest.getLocalidad(),
                    signUpRequest.getManzana(), signUpRequest.getNumero(), signUpRequest.getSolar(), signUpRequest.getApartamento(), signUpRequest.getDescripcion(), signUpRequest.getCalle());
            direccion.validate();
            direccionRepository.save(direccion);
            user.setDireccion(direccion);
        }


        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setSignUpMethod("email");
        user.setRole(role);


        try {
            user.validate();
            user.passwordValidate(user.getPassword());
            user.setPassword(encoder.encode(user.getPassword()));
            clienteRepository.save(user);
            Carrito c = new Carrito();
            c.setCliente(user);
            user.setCarrito(c);
            carritoRepository.save(c);
            return ResponseEntity.ok(new MessageResponse("¡Usuario registrado con éxito!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }


    @GetMapping("/user")
    @Operation(summary = "Obtener detalles de usuario", description = "Obtener los detalles del usuario autenticado")

    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        Usuario user = userService.findByUsername(userDetails.getUsername());
        UsuarioDTO dto=user.convertToDto();
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/username")
    @Operation(summary = "Obtener nombre de usuario", description = "Obtener el nombre de usuario del usuario autenticado")

    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/forgot-password")
    @Operation(summary = "Olvidé mi contraseña", description = "Generar un token de restablecimiento de contraseña")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgottenPasswordRequest identificador) throws Exception {
            String correo = userService.generatePasswordResetToken(identificador.getIdentificador());
            return ResponseEntity.ok(new MessageResponse("Email de restablecimiento de contraseña enviada a " + correo));

    }
    
    @PostMapping("/public/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Restablecer la contraseña usando un token")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
            userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Contraseña restablecida exitosamente"));
    }
    @PutMapping("/enable/{id}")
    public ResponseEntity<Void> habilitarUsuario(@PathVariable Long id) throws Exception {
        userService.updateAccountEnabledStatus(id,true);
        return ResponseEntity.noContent().build();
    }
}
