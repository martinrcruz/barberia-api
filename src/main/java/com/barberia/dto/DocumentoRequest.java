package com.barberia.dto;

import com.barberia.entity.Documento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoRequest {

    @NotNull(message = "La categoría del documento es obligatoria")
    private Documento.CategoriaDocumento categoriaDocumento;

    private Long sucursalId;

    private String entidadRelacionadaTipo;

    private Long entidadRelacionadaId;

    private Boolean comprimirImagen;

    private Integer calidadCompresion; // 1-100
}

