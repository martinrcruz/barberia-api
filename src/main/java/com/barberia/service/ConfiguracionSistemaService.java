package com.barberia.service;

import com.barberia.dto.ConfiguracionSistemaRequest;
import com.barberia.dto.ConfiguracionSistemaResponse;

import java.util.List;
import java.util.Map;

public interface ConfiguracionSistemaService {

    List<ConfiguracionSistemaResponse> listarTodas();

    List<ConfiguracionSistemaResponse> listarEditables();

    List<ConfiguracionSistemaResponse> listarPorCategoria(String categoria);

    ConfiguracionSistemaResponse obtenerPorId(Long id);

    ConfiguracionSistemaResponse crear(ConfiguracionSistemaRequest request);

    ConfiguracionSistemaResponse actualizar(Long id, ConfiguracionSistemaRequest request);

    void eliminar(Long id);

    Map<String, String> obtenerConfiguracionPorCategoria(String categoria);

    void guardarConfiguracionSMTP(Map<String, String> smtpConfig);

    boolean probarConfiguracionSMTP(String emailDestino);

    String obtenerValorPorClave(String clave);

    void actualizarValorPorClave(String clave, String valor);
}


