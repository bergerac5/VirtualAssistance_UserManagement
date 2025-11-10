package com.virtual.assistance.UserManagement.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.virtual.assistance.UserManagement.dtos.RoleResponse;
import com.virtual.assistance.UserManagement.exceptions.ConflictException;
import com.virtual.assistance.UserManagement.models.Role;
import com.virtual.assistance.UserManagement.repositories.RoleRepo;
import com.virtual.assistance.UserManagement.response.ApiResponse;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepo roleRepository;

    private RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(role.getId(), role.getRoleName());
    }

    // Create role
    public ApiResponse<RoleResponse> createRole(Role role) {
        Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());
        if (existingRole.isPresent()) {
            throw new ConflictException("Role with name '" + role.getRoleName() + "' already exists!");
        }

        Role savedRole = roleRepository.save(role);
        return ApiResponse.success("Role created successfully!", toRoleResponse(savedRole));
    }

    // Update role by UUID
    public ApiResponse<RoleResponse> updateRole(UUID id, Role role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ConflictException("Role with ID " + id + " not found!"));

        // Prevent duplicate role names
        Optional<Role> duplicateRole = roleRepository.findByRoleName(role.getRoleName());
        if (duplicateRole.isPresent() && !duplicateRole.get().getId().equals(id)) {
            throw new ConflictException("Another role with name '" + role.getRoleName() + "' already exists!");
        }

        existingRole.setRoleName(role.getRoleName());
        Role updatedRole = roleRepository.save(existingRole);

        return ApiResponse.success("Role updated successfully!", toRoleResponse(updatedRole));
    }

    // Delete role by UUID
    public ApiResponse<Void> deleteRole(UUID id) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ConflictException("Role with ID " + id + " not found!"));

        roleRepository.delete(existingRole);
        return ApiResponse.success("Role deleted successfully!", null);
    }

    // Get all roles
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> roleResponses = roles.stream()
                .map(this::toRoleResponse)
                .toList();
        return ApiResponse.success("Roles retrieved successfully!", roleResponses);
    }

}
