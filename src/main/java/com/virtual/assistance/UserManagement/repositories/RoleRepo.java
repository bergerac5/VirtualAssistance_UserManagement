package com.virtual.assistance.UserManagement.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virtual.assistance.UserManagement.models.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {

    // Find by role name
    Optional<Role> findByRoleName(String roleName);

    // Check if role exists by name
    boolean existsByRoleName(String roleName);

}
