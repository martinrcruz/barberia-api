package com.barberia.dto;

import com.barberia.entity.Documento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoResponse {

    private Long id;
    private String nombreOriginal;
    private String nombreArchivo;
    private String rutaArchivo;
    private String urlDescarga;
    private String tipoMime;
    private Long tamano;
    private String tamanoLegible;
    private Documento.CategoriaDocumento categoriaDocumento;
    private Long sucursalId;
    private String nombreSucursal;
    private String entidadRelacionadaTipo;
    private Long entidadRelacionadaId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

