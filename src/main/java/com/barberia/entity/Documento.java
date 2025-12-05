package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "documentos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Documento extends BaseEntity {

    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "tipo_mime", length = 100)
    private String tipoMime;

    @Column(name = "tamano")
    private Long tamano;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_documento", nullable = false)
    private CategoriaDocumento categoriaDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @Column(name = "entidad_relacionada_tipo", length = 50)
    private String entidadRelacionadaTipo;

    @Column(name = "entidad_relacionada_id")
    private Long entidadRelacionadaId;

    public enum CategoriaDocumento {
        COMPROBANTE_VENTA,
        FACTURA_COMPRA,
        CONTRATO,
        DOCUMENTO_PERSONAL,
        IMAGEN_PRODUCTO,
        OTRO
    }
}

