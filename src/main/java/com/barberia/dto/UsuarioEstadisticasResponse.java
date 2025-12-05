package com.barberia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEstadisticasResponse {
    private Double gananciaPromedioMensual;
    private List<ServicioFavoritoResponse> serviciosFavoritos;
    private Long totalVentas;
    private Double totalGanancia;
}