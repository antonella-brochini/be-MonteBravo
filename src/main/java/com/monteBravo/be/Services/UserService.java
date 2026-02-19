package com.monteBravo.be.Services;

import com.monteBravo.be.DTO.UsuarioDTO;
import com.monteBravo.be.entity.Cliente;
import com.monteBravo.be.entity.Role;
import com.monteBravo.be.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<Usuario> getAllUsers();

    List<Usuario> getAllUsersDisabled();

    UsuarioDTO getUserById(Long id) throws Exception;

    Usuario findByUsername(String username) throws Exception;

    void updateAccountLockStatus(Long userId, boolean lock) throws Exception;

    List<Role> getAllRoles();

    void updateAccountExpiryStatus(Long userId, boolean expire) throws Exception;

    void updateAccountEnabledStatus(Long userId, boolean enabled) throws Exception;

    void updateCredentialsExpiryStatus(Long userId, boolean expire) throws Exception;

    void updatePassword(Long userId, String password) throws Exception;

    String generatePasswordResetToken(String email) throws Exception;

    void resetPassword(String token, String newPassword);

    Optional<? extends Usuario> findByEmail(String email);

    Cliente registerUser(Cliente user) throws Exception;

}
