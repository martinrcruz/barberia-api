package com.barberia.service;

import com.barberia.dto.ProveedorRequest;
import com.barberia.dto.ProveedorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProveedorService {
    ProveedorResponse crear(ProveedorRequest request);
    ProveedorResponse actualizar(Long id, ProveedorRequest request);
    void eliminar(Long id);
    ProveedorResponse obtenerPorId(Long id);
    Page<ProveedorResponse> listarTodos(Pageable pageable);
    List<ProveedorResponse> listarTodosSinPaginacion();
    ProveedorResponse activarDesactivar(Long id, Boolean activo);
}

