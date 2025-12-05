package com.barberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSucursalRequest {

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 100, message = "La clave no puede exceder 100 caracteres")
    private String clave;

    @NotBlank(message = "El valor es obligatorio")
    private String valor;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Size(max = 20, message = "El tipo de dato no puede exceder 20 caracteres")
    private String tipoDato;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;

    private Boolean esObligatoria;

    private Boolean esModificable;
}

