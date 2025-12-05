package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String horarioApertura;
    private String horarioCierre;
    private String diasAtencion;
    private UsuarioBasicResponse administrador;
    private Double comisionDefecto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

