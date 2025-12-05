package com.barberia.service;

import com.barberia.dto.VentaRequest;
import com.barberia.dto.VentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    VentaResponse crearVenta(VentaRequest request);
    VentaResponse obtenerVentaPorId(Long id);
    Page<VentaResponse> listarVentas(Pageable pageable);
    List<VentaResponse> listarVentasPorSucursal(Long sucursalId);
    List<VentaResponse> listarVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    byte[] generarComprobante(Long ventaId);
    void anularVenta(Long id);
}

