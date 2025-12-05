package com.barberia.service;

import com.barberia.dto.CompraRequest;
import com.barberia.dto.CompraResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CompraService {
    CompraResponse registrarCompra(CompraRequest request);
    CompraResponse obtenerPorId(Long id);
    Page<CompraResponse> listarTodos(Pageable pageable);
    Page<CompraResponse> listarPorSucursal(Long sucursalId, Pageable pageable);
    Page<CompraResponse> listarPorProveedor(Long proveedorId, Pageable pageable);
    List<CompraResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<CompraResponse> listarPorSucursalYFechas(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

