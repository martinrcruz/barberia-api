package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "metodos_pago")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class MetodoPago extends BaseEntity {

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "es_electronico")
    private Boolean esElectronico = false; // true para débito, crédito, transferencia

    @Column(name = "requiere_referencia")
    private Boolean requiereReferencia = false; // true si requiere número de operación

    @Column(name = "orden")
    private Integer orden = 0; // Para ordenar en el frontend

    @Column(name = "icono", length = 50)
    private String icono; // Nombre del icono de Bootstrap Icons

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_metodo")
    private TipoMetodoPago tipoMetodo;

    public enum TipoMetodoPago {
        EFECTIVO,
        TARJETA_DEBITO,
        TARJETA_CREDITO,
        TRANSFERENCIA,
        CHEQUE,
        OTRO
    }
}

