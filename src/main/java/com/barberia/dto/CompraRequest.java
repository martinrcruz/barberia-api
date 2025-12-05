package com.barberia.dto;

import com.barberia.entity.Compra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequest {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    private LocalDateTime fechaCompra;

    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String numeroDocumento;

    private Compra.TipoDocumento tipoDocumento;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    @NotEmpty(message = "Debe incluir al menos un detalle de compra")
    @Valid
    private List<DetalleCompraRequest> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleCompraRequest {

        @NotNull(message = "El tipo de item es obligatorio")
        private String tipoItem; // PRODUCTO o INSUMO

        private Long productoId;

        private Long insumoId;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        @NotNull(message = "El precio unitario es obligatorio")
        private Double precioUnitario;

        private String descripcion;
    }
}

