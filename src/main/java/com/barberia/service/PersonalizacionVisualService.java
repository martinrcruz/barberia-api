package com.barberia.service;

import com.barberia.dto.PersonalizacionVisualRequest;
import com.barberia.dto.PersonalizacionVisualResponse;

import java.util.List;

public interface PersonalizacionVisualService {

    PersonalizacionVisualResponse crear(PersonalizacionVisualRequest request);

    PersonalizacionVisualResponse actualizar(Long id, PersonalizacionVisualRequest request);

    PersonalizacionVisualResponse obtenerPorId(Long id);

    PersonalizacionVisualResponse obtenerGlobal();

    PersonalizacionVisualResponse obtenerPorSucursal(Long sucursalId);

    List<PersonalizacionVisualResponse> obtenerTodas();

    void eliminar(Long id);

    PersonalizacionVisualResponse crearOActualizarGlobal(PersonalizacionVisualRequest request);

    PersonalizacionVisualResponse crearOActualizarPorSucursal(Long sucursalId, PersonalizacionVisualRequest request);
}

