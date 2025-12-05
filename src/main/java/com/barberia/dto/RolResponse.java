package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Set<PermisoResponse> permisos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

