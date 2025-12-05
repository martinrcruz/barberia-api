package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizacionVisualResponse {

    private Long id;
    private Long sucursalId;
    private String nombreSucursal;
    private String logoUrl;
    private String faviconUrl;
    private String colorPrimario;
    private String colorSecundario;
    private String colorAcento;
    private String colorTexto;
    private Boolean darkModeHabilitado;
    private String nombreEmpresa;
    private String eslogan;
    private String telefonoContacto;
    private String emailContacto;
    private String sitioWeb;
    private String direccion;
    private Boolean mostrarLogoEnComprobantes;
    private String pieDePaginaComprobante;
    private String mensajeBienvenida;
    private Boolean esGlobal;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

