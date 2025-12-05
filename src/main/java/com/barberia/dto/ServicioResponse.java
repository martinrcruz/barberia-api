package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private CategoriaResponse categoria;
    private Double precio;
    private Integer duracionMinutos;
    private Boolean tieneIva;
    private SucursalBasicResponse sucursal;
    private Set<InsumoBasicResponse> insumosUtilizados;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

