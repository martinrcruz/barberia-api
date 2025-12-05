package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSistemaResponse {

    private Long id;
    private String clave;
    private String valor;
    private String tipo;
    private String descripcion;
    private String categoria;
    private Boolean editable;
}


