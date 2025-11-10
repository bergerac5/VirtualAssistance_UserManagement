package com.virtual.assistance.UserManagement.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtual.assistance.UserManagement.dtos.UserRequest;
import com.virtual.assistance.UserManagement.exceptions.NotFoundException;
import com.virtual.assistance.UserManagement.models.Role;
import com.virtual.assistance.UserManagement.models.UserInfo;
import com.virtual.assistance.UserManagement.repositories.RoleRepo;
import com.virtual.assistance.UserManagement.response.ApiResponse;
import com.virtual.assistance.UserManagement.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepo roleRepo;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(
            @Valid @RequestBody UserRequest request, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Get the first validation error
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
        }

        try {
            UserInfo user = new UserInfo();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(request.getPassword());

            Role role = roleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new NotFoundException("Role not found with ID: " + request.getRoleId()));
            user.setRole(role);

            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send welcome email."));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable UUID id, @RequestBody UserRequest request) {
        try {
            UserInfo updates = new UserInfo();

            if (request.getName() != null)
                updates.setName(request.getName());
            if (request.getEmail() != null)
                updates.setEmail(request.getEmail());
            if (request.getPhoneNumber() != null)
                updates.setPhoneNumber(request.getPhoneNumber());
            if (request.getPassword() != null && !request.getPassword().isBlank())
                updates.setPassword(request.getPassword());
            if (request.getRoleId() != null) {
                Role role = roleRepo.findById(request.getRoleId())
                        .orElseThrow(() -> new NotFoundException("Role not found with ID: " + request.getRoleId()));
                updates.setRole(role);
            }

            return ResponseEntity.ok(userService.updateUser(id, updates));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
