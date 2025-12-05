package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

/**
 * Entidad para gestionar la personalización visual de la plataforma
 * Permite configurar logo, colores corporativos, favicon, etc.
 */
@Entity
@Table(name = "personalizacion_visual")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Audited
public class PersonalizacionVisual extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "favicon_url", length = 500)
    private String faviconUrl;

    @Column(name = "color_primario", length = 7)
    private String colorPrimario; // #08415C

    @Column(name = "color_secundario", length = 7)
    private String colorSecundario; // #EF6461

    @Column(name = "color_acento", length = 7)
    private String colorAcento; // #D5DFE5

    @Column(name = "color_texto", length = 7)
    private String colorTexto; // #FFFFFF

    @Column(name = "dark_mode_habilitado")
    private Boolean darkModeHabilitado = false;

    @Column(name = "nombre_empresa", length = 200)
    private String nombreEmpresa;

    @Column(name = "eslogan", length = 500)
    private String eslogan;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "email_contacto", length = 100)
    private String emailContacto;

    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "mostrar_logo_en_comprobantes")
    private Boolean mostrarLogoEnComprobantes = true;

    @Column(name = "pie_de_pagina_comprobante", length = 500)
    private String pieDePaginaComprobante;

    @Column(name = "mensaje_bienvenida", columnDefinition = "TEXT")
    private String mensajeBienvenida;

    /**
     * Indica si esta es la configuración global (sin sucursal específica)
     */
    @Column(name = "es_global")
    private Boolean esGlobal = false;

    @PrePersist
    @PreUpdate
    public void validarConfiguracion() {
        // Si es global, no debe tener sucursal
        if (Boolean.TRUE.equals(esGlobal)) {
            this.sucursal = null;
        }
    }
}

