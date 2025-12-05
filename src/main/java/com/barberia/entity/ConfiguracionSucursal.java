package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Entidad para gestionar la configuración específica de cada sucursal
 * Incluye horarios, comisiones, preferencias operativas, etc.
 */
@Entity
@Table(name = "configuracion_sucursal", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sucursal_id", "clave"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Audited
public class ConfiguracionSucursal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    /**
     * Clave de configuración (ej: "COMISION_DEFAULT", "HORA_APERTURA")
     */
    @Column(name = "clave", nullable = false, length = 100)
    private String clave;

    /**
     * Valor de configuración (puede ser numérico, texto, etc.)
     */
    @Column(name = "valor", nullable = false, columnDefinition = "TEXT")
    private String valor;

    /**
     * Descripción de la configuración
     */
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /**
     * Tipo de dato: STRING, NUMBER, BOOLEAN, TIME, DATE, JSON
     */
    @Column(name = "tipo_dato", length = 20)
    private String tipoDato = "STRING";

    /**
     * Categoría de configuración: HORARIOS, COMISIONES, VENTAS, INVENTARIO, GENERAL
     */
    @Column(name = "categoria", length = 50)
    private String categoria;

    /**
     * Indica si esta configuración es obligatoria
     */
    @Column(name = "es_obligatoria")
    private Boolean esObligatoria = false;

    /**
     * Indica si esta configuración puede ser modificada
     */
    @Column(name = "es_modificable")
    private Boolean esModificable = true;

    // Métodos de utilidad para obtener valores tipados

    public BigDecimal getValorComoDecimal() {
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public Integer getValorComoEntero() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Boolean getValorComoBoolean() {
        return Boolean.parseBoolean(valor);
    }

    public LocalTime getValorComoTime() {
        try {
            return LocalTime.parse(valor);
        } catch (Exception e) {
            return null;
        }
    }
}

