package com.barberia.service;

import com.barberia.dto.PerfilRequest;
import com.barberia.dto.UsuarioEstadisticasResponse;
import com.barberia.dto.UsuarioRequest;
import com.barberia.dto.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    void eliminar(Long id);
    UsuarioResponse obtenerPorId(Long id);
    Page<UsuarioResponse> listarTodos(Pageable pageable);
    List<UsuarioResponse> listarTodosSinPaginacion();
    UsuarioResponse activarDesactivar(Long id, Boolean activo);
    void bloquearCuenta(Long id);
    void desbloquearCuenta(Long id);
    UsuarioResponse actualizarMiPerfil(String email, PerfilRequest request);
    UsuarioEstadisticasResponse obtenerEstadisticas(Long usuarioId);
}

