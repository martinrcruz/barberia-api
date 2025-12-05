package com.barberia.dto;

import com.barberia.entity.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    private Boolean esElectronico;

    private Boolean requiereReferencia;

    private Integer orden;

    @Size(max = 50, message = "El icono no puede exceder 50 caracteres")
    private String icono;

    @NotNull(message = "El tipo de método de pago es obligatorio")
    private MetodoPago.TipoMetodoPago tipoMetodo;
}

