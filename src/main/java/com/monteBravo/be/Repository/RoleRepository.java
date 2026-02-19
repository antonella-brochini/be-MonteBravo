package com.monteBravo.be.Repository;

import com.monteBravo.be.entity.AppRole;
import com.monteBravo.be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}