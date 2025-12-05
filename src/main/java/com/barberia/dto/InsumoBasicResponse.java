package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoBasicResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String unidadMedida;
}

