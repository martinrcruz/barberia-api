package com.barberia.dto;

import com.barberia.entity.RegistroJornada;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroJornadaResponse {

    private Long id;
    private UsuarioBasicResponse usuario;
    private SucursalBasicResponse sucursal;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private Double horasTrabajadas;
    private String observaciones;
    private RegistroJornada.EstadoJornada estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

