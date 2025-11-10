package com.virtual.assistance.UserManagement.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.virtual.assistance.UserManagement.dtos.RoleResponse;
import com.virtual.assistance.UserManagement.dtos.UserResponse;
import com.virtual.assistance.UserManagement.exceptions.ConflictException;
import com.virtual.assistance.UserManagement.exceptions.NotFoundException;
import com.virtual.assistance.UserManagement.models.Role;
import com.virtual.assistance.UserManagement.models.UserInfo;
import com.virtual.assistance.UserManagement.repositories.UserRepository;
import com.virtual.assistance.UserManagement.response.ApiResponse;

import jakarta.mail.MessagingException;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Convert UserInfo to UserResponse
    private UserResponse toUserResponse(UserInfo user) {
        Role role = user.getRole();
        RoleResponse roleResponse = new RoleResponse(role.getId(), role.getRoleName());
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), roleResponse);
    }

    // CREATE user
    public ApiResponse<UserResponse> createUser(UserInfo userInfo) throws MessagingException {
        if (userRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new ConflictException("User with email '" + userInfo.getEmail() + "' already exists!");
        }

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        UserInfo savedUser = userRepository.save(userInfo);

        // Build and send HTML email
        String subject = "Welcome to Virtual Assistance";
        String htmlContent = emailService.buildWelcomeEmail(savedUser.getName(), savedUser.getEmail());
        emailService.sendHtmlEmail(savedUser.getEmail(), subject, htmlContent);

        return ApiResponse.success("User created successfully!", toUserResponse(savedUser));
    }

    // READ all users
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Users retrieved successfully!", users);
    }

    // READ one user by ID
    public ApiResponse<UserResponse> getUserById(UUID id) {
        UserInfo user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        return ApiResponse.success("User retrieved successfully!", toUserResponse(user));
    }

    // UPDATE user
    public ApiResponse<UserResponse> updateUser(UUID id, UserInfo updates) {
        UserInfo existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        if (updates.getEmail() != null) {
            userRepository.findByEmail(updates.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("Email '" + updates.getEmail() + "' is already used by another user!");
                }
            });
            existingUser.setEmail(updates.getEmail());
        }

        if (updates.getName() != null)
            existingUser.setName(updates.getName());
        if (updates.getPhoneNumber() != null)
            existingUser.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getPassword() != null && !updates.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updates.getPassword()));
        }
        if (updates.getRole() != null)
            existingUser.setRole(updates.getRole());

        UserInfo updatedUser = userRepository.save(existingUser);
        return ApiResponse.success("User updated successfully!", toUserResponse(updatedUser));
    }

    // DELETE user
    public ApiResponse<Void> deleteUser(UUID id) {
        UserInfo user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
        return ApiResponse.success("User deleted successfully!", null);
    }

}
