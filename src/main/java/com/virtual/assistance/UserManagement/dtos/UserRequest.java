package com.virtual.assistance.UserManagement.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private UUID roleId;
}