package com.virtual.assistance.UserManagement.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class RoleResponse {
    private UUID id;
    private String roleName;

}
