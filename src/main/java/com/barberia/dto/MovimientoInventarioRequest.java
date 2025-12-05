package com.barberia.dto;

import com.barberia.entity.MovimientoInventario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MovimientoInventario.TipoMovimiento tipoMovimiento;

    @NotNull(message = "El tipo de item es obligatorio")
    private MovimientoInventario.TipoItemInventario tipoItem;

    private Long productoId;

    private Long insumoId;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    @NotNull(message = "El motivo es obligatorio")
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    private String motivo;

    @Size(max = 100, message = "La referencia del documento no puede exceder 100 caracteres")
    private String referenciaDocumento;

    private LocalDateTime fechaMovimiento;
}

