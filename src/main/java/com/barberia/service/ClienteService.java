package com.barberia.service;

import com.barberia.dto.ClienteRequest;
import com.barberia.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    ClienteResponse crear(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    void eliminar(Long id);

    ClienteResponse obtenerPorId(Long id);

    Page<ClienteResponse> listarTodos(Pageable pageable);
}


