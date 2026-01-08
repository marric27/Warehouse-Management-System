package com.relatech.warehouse_management_system.security.repo;

import com.relatech.warehouse_management_system.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
