package com.virtual.assistance.UserManagement.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.virtual.assistance.UserManagement.dtos.RoleResponse;
import com.virtual.assistance.UserManagement.exceptions.ConflictException;
import com.virtual.assistance.UserManagement.models.Role;
import com.virtual.assistance.UserManagement.response.ApiResponse;
import com.virtual.assistance.UserManagement.services.RoleService;

@Controller
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody Role role) {
        try {
            ApiResponse<RoleResponse> response = roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable UUID id, @RequestBody Role role) {
        try {
            ApiResponse<RoleResponse> response = roleService.updateRole(id, role);
            return ResponseEntity.ok(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        try {
            ApiResponse<Void> response = roleService.deleteRole(id);
            return ResponseEntity.ok(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        ApiResponse<List<RoleResponse>> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }
}
