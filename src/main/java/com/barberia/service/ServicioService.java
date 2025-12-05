package com.barberia.service;

import com.barberia.dto.ServicioRequest;
import com.barberia.dto.ServicioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServicioService {

    ServicioResponse crear(ServicioRequest request);

    ServicioResponse actualizar(Long id, ServicioRequest request);

    void eliminar(Long id);

    ServicioResponse obtenerPorId(Long id);

    Page<ServicioResponse> listarTodos(Pageable pageable);

    Page<ServicioResponse> listarPorSucursal(Long sucursalId, Pageable pageable);

    List<ServicioResponse> listarPorSucursalSinPaginacion(Long sucursalId);

    ServicioResponse buscarPorCodigo(String codigo);
}

