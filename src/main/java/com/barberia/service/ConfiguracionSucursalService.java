package com.barberia.service;

import com.barberia.dto.ConfiguracionSucursalRequest;
import com.barberia.dto.ConfiguracionSucursalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ConfiguracionSucursalService {

    ConfiguracionSucursalResponse crear(ConfiguracionSucursalRequest request);

    ConfiguracionSucursalResponse actualizar(Long id, ConfiguracionSucursalRequest request);

    ConfiguracionSucursalResponse obtenerPorId(Long id);

    ConfiguracionSucursalResponse obtenerPorSucursalYClave(Long sucursalId, String clave);

    List<ConfiguracionSucursalResponse> obtenerPorSucursal(Long sucursalId);

    Page<ConfiguracionSucursalResponse> obtenerPorSucursalPaginado(Long sucursalId, Pageable pageable);

    List<ConfiguracionSucursalResponse> obtenerPorSucursalYCategoria(Long sucursalId, String categoria);

    Page<ConfiguracionSucursalResponse> buscarPorPalabraClave(Long sucursalId, String keyword, Pageable pageable);

    void eliminar(Long id);

    String obtenerValor(Long sucursalId, String clave, String valorPorDefecto);

    ConfiguracionSucursalResponse crearOActualizar(Long sucursalId, String clave, String valor);

    void inicializarConfiguracionesPorDefecto(Long sucursalId);

    Map<String, String> obtenerConfiguracionComoMapa(Long sucursalId);

    List<ConfiguracionSucursalResponse> obtenerConfiguracionesObligatorias(Long sucursalId);
}

