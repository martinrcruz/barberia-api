package com.barberia.service;

import com.barberia.dto.SucursalRequest;
import com.barberia.dto.SucursalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SucursalService {

    SucursalResponse crear(SucursalRequest request);

    SucursalResponse actualizar(Long id, SucursalRequest request);

    void eliminar(Long id);

    SucursalResponse obtenerPorId(Long id);

    Page<SucursalResponse> listarTodos(Pageable pageable);

    List<SucursalResponse> listarTodasSinPaginacion();
}

