package com.barberia.service;

import com.barberia.dto.PermisoRequest;
import com.barberia.dto.PermisoResponse;
import com.barberia.entity.Permiso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermisoService {
    PermisoResponse crear(PermisoRequest request);
    PermisoResponse actualizar(Long id, PermisoRequest request);
    void eliminar(Long id);
    PermisoResponse obtenerPorId(Long id);
    Page<PermisoResponse> listarTodos(Pageable pageable);
    List<PermisoResponse> listarTodosSinPaginacion();
    List<PermisoResponse> listarPorTipo(Permiso.TipoPermiso tipo);
}

