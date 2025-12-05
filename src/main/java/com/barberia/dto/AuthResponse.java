package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String tipo;
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private Set<String> roles;
    private Set<String> permisos;
}

