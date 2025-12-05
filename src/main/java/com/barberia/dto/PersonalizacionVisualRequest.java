package com.barberia.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizacionVisualRequest {

    private Long sucursalId;

    @Size(max = 500, message = "La URL del logo no puede exceder 500 caracteres")
    private String logoUrl;

    @Size(max = 500, message = "La URL del favicon no puede exceder 500 caracteres")
    private String faviconUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color primario debe ser un color hexadecimal válido")
    private String colorPrimario;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color secundario debe ser un color hexadecimal válido")
    private String colorSecundario;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color de acento debe ser un color hexadecimal válido")
    private String colorAcento;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color de texto debe ser un color hexadecimal válido")
    private String colorTexto;

    private Boolean darkModeHabilitado;

    @Size(max = 200, message = "El nombre de la empresa no puede exceder 200 caracteres")
    private String nombreEmpresa;

    @Size(max = 500, message = "El eslogan no puede exceder 500 caracteres")
    private String eslogan;

    @Size(max = 20, message = "El teléfono de contacto no puede exceder 20 caracteres")
    private String telefonoContacto;

    @Size(max = 100, message = "El email de contacto no puede exceder 100 caracteres")
    private String emailContacto;

    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    private String sitioWeb;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccion;

    private Boolean mostrarLogoEnComprobantes;

    @Size(max = 500, message = "El pie de página del comprobante no puede exceder 500 caracteres")
    private String pieDePaginaComprobante;

    private String mensajeBienvenida;

    private Boolean esGlobal;
}

