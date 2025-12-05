package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private CategoriaResponse categoria;
    private Integer stockActual;
    private Integer stockMinimo;
    private String unidadMedida;
    private Double precioUnitario;
    private SucursalBasicResponse sucursal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean stockBajo;

    public Boolean getStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }
}

