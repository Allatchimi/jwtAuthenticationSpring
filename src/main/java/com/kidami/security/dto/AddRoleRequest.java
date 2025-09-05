package com.kidami.security.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AddRoleRequest {
    private String email;
    private Set<String> roles; // Using strings to represent role names

}
