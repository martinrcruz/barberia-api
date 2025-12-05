package com.barberia.dto;

import com.barberia.entity.Permiso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoResponse {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Permiso.TipoPermiso tipo;
    private String recurso;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

