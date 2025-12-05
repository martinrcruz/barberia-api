package com.barberia.service;

import com.barberia.dto.RegistroJornadaRequest;
import com.barberia.dto.RegistroJornadaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroJornadaService {
    RegistroJornadaResponse registrarEntrada(Long usuarioId, Long sucursalId, String observaciones);
    RegistroJornadaResponse registrarSalida(Long usuarioId, Long sucursalId, String observaciones);
    RegistroJornadaResponse obtenerPorId(Long id);
    Page<RegistroJornadaResponse> listarTodos(Pageable pageable);
    Page<RegistroJornadaResponse> listarPorUsuario(Long usuarioId, Pageable pageable);
    Page<RegistroJornadaResponse> listarPorSucursal(Long sucursalId, Pageable pageable);
    Page<RegistroJornadaResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    List<RegistroJornadaResponse> listarPorUsuarioYFechas(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<RegistroJornadaResponse> listarPorSucursalYFechas(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    void cancelarJornada(Long id, String motivo);
}

