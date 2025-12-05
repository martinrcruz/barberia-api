package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProductoResponse {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String nombre;
    private String sku;
    private Double precioVenta;
    private Double precioCosto;
    private Double precioVentaEfectivo;
    private Double precioCostoEfectivo;
    private Integer stockActual;
    private Integer stockMinimo;
    private String atributos;
    private String imagenUrl;
    private Integer orden;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

