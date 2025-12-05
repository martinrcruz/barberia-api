package com.barberia.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {

    @NotNull(message = "El trabajador es obligatorio")
    private Long trabajadorId;

    private Long clienteId;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;

    @NotEmpty(message = "Debe incluir al menos un detalle")
    private List<DetalleVentaRequest> detalles;

    private String observaciones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleVentaRequest {
        @NotNull
        private String tipoItem; // PRODUCTO o SERVICIO

        private Long productoId;
        private Long servicioId;
        private String descripcion;

        @NotNull
        private Integer cantidad;

        @NotNull
        private Double precioUnitario;

        private Boolean aplicaIva;
    }
}

