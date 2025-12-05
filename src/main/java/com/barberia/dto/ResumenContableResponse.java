package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenContableResponse {

    private Double totalIngresos;
    private Double totalEgresos;
    private Double balance;

    // Nuevos campos para estadísticas de dashboard
    private Double gananciaNeta;
    private Long cantidadRegistros;
}


