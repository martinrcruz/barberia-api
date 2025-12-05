package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {
    private Long id;
    private String numeroVenta;
    private LocalDateTime fechaVenta;
    private String trabajadorNombre;
    private String clienteNombre;
    private String sucursalNombre;
    private Double subtotal;
    private Double iva;
    private Double total;
    private Double comisionTrabajador;
    private String metodoPago;
    private String observaciones;
    private List<DetalleVentaResponse> detalles;
    private String comprobanteUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleVentaResponse {
        private Long id;
        private String tipoItem;
        private String descripcion;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
        private Boolean aplicaIva;
    }
}

