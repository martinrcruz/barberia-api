package com.barberia.service;

import com.barberia.dto.MetodoPagoRequest;
import com.barberia.dto.MetodoPagoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MetodoPagoService {
    MetodoPagoResponse crear(MetodoPagoRequest request);
    MetodoPagoResponse actualizar(Long id, MetodoPagoRequest request);
    void eliminar(Long id);
    MetodoPagoResponse obtenerPorId(Long id);
    Page<MetodoPagoResponse> listarTodos(Pageable pageable);
    List<MetodoPagoResponse> listarActivosOrdenados();
    MetodoPagoResponse activarDesactivar(Long id, Boolean activo);
}

