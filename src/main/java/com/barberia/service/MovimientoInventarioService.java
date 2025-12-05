package com.barberia.service;

import com.barberia.dto.MovimientoInventarioRequest;
import com.barberia.dto.MovimientoInventarioResponse;
import com.barberia.entity.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioService {
    MovimientoInventarioResponse registrarAjuste(MovimientoInventarioRequest request);
    MovimientoInventarioResponse obtenerPorId(Long id);
    Page<MovimientoInventarioResponse> listarTodos(Pageable pageable);
    Page<MovimientoInventarioResponse> listarPorSucursal(Long sucursalId, Pageable pageable);
    List<MovimientoInventarioResponse> listarKardexPorProducto(Long productoId);
    List<MovimientoInventarioResponse> listarKardexPorInsumo(Long insumoId);
    List<MovimientoInventarioResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Page<MovimientoInventarioResponse> listarPorTipo(MovimientoInventario.TipoMovimiento tipoMovimiento, Pageable pageable);
}

