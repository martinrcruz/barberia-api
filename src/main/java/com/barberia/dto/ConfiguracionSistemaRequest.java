package com.barberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfiguracionSistemaRequest {

    @NotBlank
    @Size(max = 100)
    private String clave;

    private String valor;

    @Size(max = 50)
    private String tipo;

    @Size(max = 255)
    private String descripcion;

    @Size(max = 50)
    private String categoria;

    private Boolean editable;
}


