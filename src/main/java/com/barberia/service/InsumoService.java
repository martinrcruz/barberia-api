package com.barberia.service;

import com.barberia.dto.InsumoRequest;
import com.barberia.dto.InsumoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InsumoService {

    InsumoResponse crear(InsumoRequest request);

    InsumoResponse actualizar(Long id, InsumoRequest request);

    void eliminar(Long id);

    InsumoResponse obtenerPorId(Long id);

    Page<InsumoResponse> listarTodos(Pageable pageable);

    Page<InsumoResponse> listarPorSucursal(Long sucursalId, Pageable pageable);

    List<InsumoResponse> listarPorSucursalSinPaginacion(Long sucursalId);

    List<InsumoResponse> listarConStockBajo(Long sucursalId);

    InsumoResponse actualizarStock(Long id, Integer cantidad, String tipo);
}

