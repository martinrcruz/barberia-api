package com.barberia.dto;

import com.barberia.entity.MovimientoInventario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioResponse {

    private Long id;
    private LocalDateTime fechaMovimiento;
    private MovimientoInventario.TipoMovimiento tipoMovimiento;
    private MovimientoInventario.TipoItemInventario tipoItem;
    private ProductoResponse producto;
    private InsumoResponse insumo;
    private SucursalBasicResponse sucursal;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String motivo;
    private String referenciaDocumento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

