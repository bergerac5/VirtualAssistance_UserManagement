package com.virtual.assistance.UserManagement.dtos;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private RoleResponse role;

    public UserResponse(UUID id, String name, String email, String phoneNumber, RoleResponse role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RoleResponse getRole() {
        return role;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRole(RoleResponse role) {
        this.role = role;
    }

}