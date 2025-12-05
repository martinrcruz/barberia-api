package com.barberia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteProductoRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre de la variante es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String sku;

    private Double precioVenta;

    private Double precioCosto;

    private Integer stockActual;

    private Integer stockMinimo;

    @Size(max = 1000, message = "Los atributos no pueden exceder 1000 caracteres")
    private String atributos;

    @Size(max = 255, message = "La URL de imagen no puede exceder 255 caracteres")
    private String imagenUrl;

    private Integer orden;
}

