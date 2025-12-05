package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroContableResponse {

    private Long id;
    private LocalDateTime fechaRegistro;
    private String tipoMovimiento;
    private String categoria;
    private BigDecimal monto;
    private String concepto;
    private Long sucursalId;
    private String nombreSucursal;
    private Long ventaId;
    private Long compraId;
    private String referenciaMovimiento;
}


