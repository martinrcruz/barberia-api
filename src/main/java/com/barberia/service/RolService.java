package com.barberia.service;

import com.barberia.dto.RolRequest;
import com.barberia.dto.RolResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolService {
    RolResponse crear(RolRequest request);
    RolResponse actualizar(Long id, RolRequest request);
    void eliminar(Long id);
    RolResponse obtenerPorId(Long id);
    Page<RolResponse> listarTodos(Pageable pageable);
    List<RolResponse> listarTodosSinPaginacion();
    RolResponse clonar(Long id, String nuevoNombre, String nuevoCodigo);
    RolResponse agregarPermisos(Long rolId, List<Long> permisosIds);
    RolResponse removerPermisos(Long rolId, List<Long> permisosIds);
}

