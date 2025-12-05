package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private CategoriaResponse categoria;
    private Double precioVenta;
    private Double precioCosto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean tieneIva;
    private String imagenUrl;
    private SucursalBasicResponse sucursal;
    private String unidadMedida;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean stockBajo;

    public Boolean getStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }
}

