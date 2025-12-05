package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String telefono;
    private String rut;
    private String direccion;
    private String nacionalidad;
    private String fotoPerfil;
    private Set<RolResponse> roles;
    private Set<SucursalBasicResponse> sucursales;
    private Boolean cuentaBloqueada;
    private Boolean activo;
    private Double porcentajeComision;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

