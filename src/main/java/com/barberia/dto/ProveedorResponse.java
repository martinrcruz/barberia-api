package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Long id;
    private String rut;
    private String razonSocial;
    private String nombreFantasia;
    private String email;
    private String telefono;
    private String direccion;
    private String contactoNombre;
    private String contactoTelefono;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

