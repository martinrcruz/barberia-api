package com.barberia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_jornadas")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class RegistroJornada extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoJornada estado = EstadoJornada.ACTIVA;

    public enum EstadoJornada {
        ACTIVA,      // Check-in realizado, aún no hay check-out
        FINALIZADA,  // Check-out realizado
        CANCELADA    // Jornada cancelada por algún motivo
    }

    /**
     * Calcula las horas trabajadas cuando se registra la salida
     */
    public void calcularHorasTrabajadas() {
        if (fechaEntrada != null && fechaSalida != null) {
            long minutos = java.time.Duration.between(fechaEntrada, fechaSalida).toMinutes();
            this.horasTrabajadas = minutos / 60.0;
        }
    }
}

