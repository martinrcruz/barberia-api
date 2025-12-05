package com.barberia.service;

import com.barberia.dto.RegistroContableResponse;
import com.barberia.dto.ResumenContableResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ContabilidadService {

    List<RegistroContableResponse> listarRegistros(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    ResumenContableResponse obtenerResumen(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}


