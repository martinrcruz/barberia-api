package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSucursalResponse {

    private Long id;
    private Long sucursalId;
    private String nombreSucursal;
    private String clave;
    private String valor;
    private String descripcion;
    private String tipoDato;
    private String categoria;
    private Boolean esObligatoria;
    private Boolean esModificable;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

