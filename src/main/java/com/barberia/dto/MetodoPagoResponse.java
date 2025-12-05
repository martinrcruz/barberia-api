package com.barberia.dto;

import com.barberia.entity.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoResponse {

    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Boolean esElectronico;
    private Boolean requiereReferencia;
    private Integer orden;
    private String icono;
    private MetodoPago.TipoMetodoPago tipoMetodo;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

