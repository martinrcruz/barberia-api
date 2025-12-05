package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioFavoritoResponse {
    private Long servicioId;
    private String servicioNombre;
    private Long cantidadVentas;
}

