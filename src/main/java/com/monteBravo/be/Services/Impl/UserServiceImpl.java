package com.monteBravo.be.Services.Impl;

import com.monteBravo.be.DTO.UsuarioDTO;
import com.monteBravo.be.Exceptions.UserNotFoundException;
import com.monteBravo.be.Exceptions.UsuarioException;
import com.monteBravo.be.Repository.*;
import com.monteBravo.be.Services.SendEmail;
import com.monteBravo.be.Services.UserService;
import com.monteBravo.be.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    @Value("${frontend.url}")
    String frontendUrl;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    CarritoRepository  carritoRepository;

    @Autowired
    AdministradorRepository administradorRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    SendEmail emailService;




    @Override
    public void updateUserRole(Long userId, String roleName) {
        Cliente user = clienteRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("Usuario no encontrado"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        user.setRole(role);
        clienteRepository.save(user);
    }

    @Override
    public List<Usuario> getAllUsers() {
        List< Usuario> users = new ArrayList<>();
        users.addAll(clienteRepository.findAll());
        users.addAll(administradorRepository.findAll());
        return users;
    }
    @Override
    public List<Usuario> getAllUsersDisabled() {
        List<Usuario> users = Stream.concat(
                clienteRepository.findAllDisabled().stream(),
                administradorRepository.findAllDisabled().stream()
        ).collect(Collectors.toList());
        return users;
    }


    @Override
    public UsuarioDTO getUserById(Long id) throws Exception {
        Usuario user = clienteRepository.findById(id)
                .map(cliente -> (Usuario) cliente) // Cast explícito
                .or(() -> administradorRepository.findById(id))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return user.convertToDto();
    }



    @Override
    public Usuario findByUsername(String username) throws Exception {
        Usuario user = clienteRepository.findByNombreUsuario(username)
                .map(cliente -> (Usuario) cliente) // Cast explícito
                .or(() -> administradorRepository.findByNombreUsuario(username))
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return user;
    }


    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) throws Exception {
        Cliente c = clienteRepository.findById(userId).orElse(null);
        if (c == null) {
            Administrador a = administradorRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            a.setAccountNonLocked(!lock);
            administradorRepository.save(a);
        } else {
            c.setAccountNonLocked(!lock);
            clienteRepository.save(c);
        }
    }


    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) throws Exception {
        Cliente c = clienteRepository.findById(userId).orElse(null);
        if (c == null) {
            Administrador a = administradorRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            a.setAccountNonExpired(!expire);
            administradorRepository.save(a);
        } else {
            c.setAccountNonExpired(!expire);
            clienteRepository.save(c);
        }
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) throws Exception {
        Cliente c = clienteRepository.findById(userId).orElse(null);
        if (c == null) {
            Administrador a = administradorRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            a.setEnabled(enabled);
            administradorRepository.save(a);
        } else {
            c.setEnabled(enabled);
            clienteRepository.save(c);
        }

    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) throws Exception {
        Cliente c = clienteRepository.findById(userId).orElse(null);
        if (c == null) {
            Administrador a = administradorRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            a.setCredentialsNonExpired(!expire);
            administradorRepository.save(a);
        } else {
            c.setCredentialsNonExpired(!expire);
            clienteRepository.save(c);
        }
    }


    @Override
    public void updatePassword(Long userId, String password) throws Exception{

        try {
            Cliente c = clienteRepository.findById(userId).orElse(null);
            if (c == null) {
                Administrador a = administradorRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

                a.setPassword(passwordEncoder.encode(password));
                administradorRepository.save(a);
            }else {
                c.setPassword(passwordEncoder.encode(password));
                clienteRepository.save(c);
            }
        } catch (Exception e) {
            throw new RuntimeException("No ha sido posible restablecer contraseña");
        }
    }

    @Override
    public String generatePasswordResetToken(String identificador) throws Exception {
        // Buscar usuario por email o nombre de usuario
        Usuario user = identificador.contains("@")
                ? clienteRepository.findByEmail(identificador)
                .map(cliente -> (Usuario) cliente)
                .or(() -> administradorRepository.findByEmail(identificador))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identificador))
                : clienteRepository.findByNombreUsuario(identificador)
                .map(cliente -> (Usuario) cliente)
                .or(() -> administradorRepository.findByNombreUsuario(identificador))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identificador));

        // Validar si el usuario está habilitado
        if (!user.isEnabled()) {
            throw new IllegalStateException("El usuario está deshabilitado. Contacte con un administrador.");
        }

        // Generar token de restablecimiento
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);

        // Construir URL de restablecimiento
        String resetUrl = frontendUrl + "/user/reset-password?token=" + token;


         emailService.sendPasswordResetEmail(user, resetUrl);

        return maskEmail(user.getEmail());
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de reseteo de contraseña invalido"));
        Usuario user = resetToken.getUser();
        if (resetToken.isUsed())
            throw new RuntimeException("Token ya ha sido utilizado");

        if (resetToken.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Token ha expirado");
        try {
            user.passwordValidate(newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));

            if (user instanceof Cliente c) {
                clienteRepository.save(c);
            }else if (user instanceof Administrador a) {
                administradorRepository.save(a);
            }else throw new RuntimeException("Tipo de usuario no soportado");
            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);
        } catch (UsuarioException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<? extends Usuario> findByEmail(String email) {

        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
        return clienteOpt.isPresent() ? clienteOpt : administradorOpt;
    }


    @Override
    public Cliente registerUser(Cliente user) throws Exception {
        user.validate();
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        Carrito c = new Carrito();
        c.setCliente(user);
        user.setCarrito(c);
        clienteRepository.save(user);
        carritoRepository.save(c);
        return user;
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // No enmascarar si el correo es muy corto
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "****" + domain; // Si el localPart es corto
        }

        return localPart.charAt(0) + "****" + localPart.charAt(localPart.length() - 1) + domain;
    }

}
