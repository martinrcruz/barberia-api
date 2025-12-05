package com.barberia.service.impl;

import com.barberia.dto.ConfiguracionSucursalRequest;
import com.barberia.dto.ConfiguracionSucursalResponse;
import com.barberia.entity.ConfiguracionSucursal;
import com.barberia.entity.Sucursal;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.ConfiguracionSucursalRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.ConfiguracionSucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfiguracionSucursalServiceImpl implements ConfiguracionSucursalService {

    private final ConfiguracionSucursalRepository configuracionRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    public ConfiguracionSucursalResponse crear(ConfiguracionSucursalRequest request) {
        log.info("Creando nueva configuración para sucursal ID: {}, clave: {}", request.getSucursalId(), request.getClave());

        // Validar que no exista ya la configuración
        if (configuracionRepository.existsBySucursalIdAndClaveAndActiveTrue(request.getSucursalId(), request.getClave())) {
            throw new BusinessException("Ya existe una configuración con la clave '" + request.getClave() + "' para esta sucursal");
        }

        ConfiguracionSucursal configuracion = mapearEntidad(request);
        ConfiguracionSucursal guardada = configuracionRepository.save(configuracion);
        log.info("Configuración creada exitosamente con ID: {}", guardada.getId());

        return mapearRespuesta(guardada);
    }

    @Override
    public ConfiguracionSucursalResponse actualizar(Long id, ConfiguracionSucursalRequest request) {
        log.info("Actualizando configuración con ID: {}", id);

        ConfiguracionSucursal configuracion = configuracionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con ID: " + id));

        // Validar si es modificable
        if (Boolean.FALSE.equals(configuracion.getEsModificable())) {
            throw new BusinessException("Esta configuración no puede ser modificada");
        }

        actualizarEntidad(configuracion, request);
        ConfiguracionSucursal actualizada = configuracionRepository.save(configuracion);
        log.info("Configuración actualizada exitosamente");

        return mapearRespuesta(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionSucursalResponse obtenerPorId(Long id) {
        log.info("Obteniendo configuración con ID: {}", id);

        ConfiguracionSucursal configuracion = configuracionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con ID: " + id));

        return mapearRespuesta(configuracion);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionSucursalResponse obtenerPorSucursalYClave(Long sucursalId, String clave) {
        log.info("Obteniendo configuración para sucursal ID: {}, clave: {}", sucursalId, clave);

        return configuracionRepository.findBySucursalIdAndClave(sucursalId, clave)
            .map(this::mapearRespuesta)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Configuración no encontrada para sucursal ID: " + sucursalId + " y clave: " + clave));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSucursalResponse> obtenerPorSucursal(Long sucursalId) {
        log.info("Obteniendo todas las configuraciones para sucursal ID: {}", sucursalId);

        return configuracionRepository.findAllBySucursalId(sucursalId).stream()
            .map(this::mapearRespuesta)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfiguracionSucursalResponse> obtenerPorSucursalPaginado(Long sucursalId, Pageable pageable) {
        log.info("Obteniendo configuraciones paginadas para sucursal ID: {}", sucursalId);

        return configuracionRepository.findBySucursalId(sucursalId, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSucursalResponse> obtenerPorSucursalYCategoria(Long sucursalId, String categoria) {
        log.info("Obteniendo configuraciones para sucursal ID: {} y categoría: {}", sucursalId, categoria);

        return configuracionRepository.findBySucursalIdAndCategoria(sucursalId, categoria).stream()
            .map(this::mapearRespuesta)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfiguracionSucursalResponse> buscarPorPalabraClave(Long sucursalId, String keyword, Pageable pageable) {
        log.info("Buscando configuraciones para sucursal ID: {} con palabra clave: {}", sucursalId, keyword);

        return configuracionRepository.searchByKeyword(sucursalId, keyword, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando configuración con ID: {}", id);

        ConfiguracionSucursal configuracion = configuracionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con ID: " + id));

        // Validar si es obligatoria
        if (Boolean.TRUE.equals(configuracion.getEsObligatoria())) {
            throw new BusinessException("No se puede eliminar una configuración obligatoria");
        }

        configuracion.setActive(false);
        configuracionRepository.save(configuracion);
        log.info("Configuración eliminada (desactivada) exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public String obtenerValor(Long sucursalId, String clave, String valorPorDefecto) {
        return configuracionRepository.findBySucursalIdAndClave(sucursalId, clave)
            .map(ConfiguracionSucursal::getValor)
            .orElse(valorPorDefecto);
    }

    @Override
    public ConfiguracionSucursalResponse crearOActualizar(Long sucursalId, String clave, String valor) {
        log.info("Creando o actualizando configuración para sucursal ID: {}, clave: {}", sucursalId, clave);

        return configuracionRepository.findBySucursalIdAndClave(sucursalId, clave)
            .map(existente -> {
                if (Boolean.FALSE.equals(existente.getEsModificable())) {
                    throw new BusinessException("Esta configuración no puede ser modificada");
                }
                existente.setValor(valor);
                return mapearRespuesta(configuracionRepository.save(existente));
            })
            .orElseGet(() -> {
                ConfiguracionSucursalRequest request = new ConfiguracionSucursalRequest();
                request.setSucursalId(sucursalId);
                request.setClave(clave);
                request.setValor(valor);
                request.setTipoDato("STRING");
                request.setEsModificable(true);
                request.setEsObligatoria(false);
                return crear(request);
            });
    }

    @Override
    public void inicializarConfiguracionesPorDefecto(Long sucursalId) {
        log.info("Inicializando configuraciones por defecto para sucursal ID: {}", sucursalId);

        // Verificar que la sucursal existe
        sucursalRepository.findById(sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));

        // Configuraciones por defecto
        crearConfiguracionSiNoExiste(sucursalId, "COMISION_DEFAULT", "10", "Comisión por defecto para trabajadores (%)", "NUMBER", "COMISIONES", false, true);
        crearConfiguracionSiNoExiste(sucursalId, "HORA_APERTURA", "09:00", "Hora de apertura de la sucursal", "TIME", "HORARIOS", true, true);
        crearConfiguracionSiNoExiste(sucursalId, "HORA_CIERRE", "20:00", "Hora de cierre de la sucursal", "TIME", "HORARIOS", true, true);
        crearConfiguracionSiNoExiste(sucursalId, "IVA_INCLUIDO", "true", "IVA incluido en precios", "BOOLEAN", "VENTAS", false, true);
        crearConfiguracionSiNoExiste(sucursalId, "STOCK_MINIMO_ALERTA", "5", "Stock mínimo para generar alerta", "NUMBER", "INVENTARIO", false, true);
        crearConfiguracionSiNoExiste(sucursalId, "PERMITIR_STOCK_NEGATIVO", "false", "Permitir ventas con stock negativo", "BOOLEAN", "VENTAS", false, true);
        crearConfiguracionSiNoExiste(sucursalId, "TIEMPO_ESPERA_PROMEDIO", "30", "Tiempo de espera promedio por servicio (minutos)", "NUMBER", "GENERAL", false, true);

        log.info("Configuraciones por defecto inicializadas exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> obtenerConfiguracionComoMapa(Long sucursalId) {
        log.info("Obteniendo configuración como mapa para sucursal ID: {}", sucursalId);

        List<ConfiguracionSucursal> configuraciones = configuracionRepository.findAllBySucursalId(sucursalId);
        Map<String, String> mapa = new HashMap<>();

        for (ConfiguracionSucursal config : configuraciones) {
            mapa.put(config.getClave(), config.getValor());
        }

        return mapa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionSucursalResponse> obtenerConfiguracionesObligatorias(Long sucursalId) {
        log.info("Obteniendo configuraciones obligatorias para sucursal ID: {}", sucursalId);

        return configuracionRepository.findConfiguracionesObligatorias(sucursalId).stream()
            .map(this::mapearRespuesta)
            .collect(Collectors.toList());
    }

    // Métodos auxiliares

    private void crearConfiguracionSiNoExiste(Long sucursalId, String clave, String valor, String descripcion, String tipoDato, String categoria, boolean esObligatoria, boolean esModificable) {
        if (!configuracionRepository.existsBySucursalIdAndClaveAndActiveTrue(sucursalId, clave)) {
            ConfiguracionSucursalRequest request = new ConfiguracionSucursalRequest();
            request.setSucursalId(sucursalId);
            request.setClave(clave);
            request.setValor(valor);
            request.setDescripcion(descripcion);
            request.setTipoDato(tipoDato);
            request.setCategoria(categoria);
            request.setEsObligatoria(esObligatoria);
            request.setEsModificable(esModificable);
            crear(request);
        }
    }

    private ConfiguracionSucursal mapearEntidad(ConfiguracionSucursalRequest request) {
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        ConfiguracionSucursal configuracion = new ConfiguracionSucursal();
        configuracion.setSucursal(sucursal);
        configuracion.setClave(request.getClave().toUpperCase().replace(" ", "_"));
        configuracion.setValor(request.getValor());
        configuracion.setDescripcion(request.getDescripcion());
        configuracion.setTipoDato(request.getTipoDato() != null ? request.getTipoDato() : "STRING");
        configuracion.setCategoria(request.getCategoria());
        configuracion.setEsObligatoria(request.getEsObligatoria() != null ? request.getEsObligatoria() : false);
        configuracion.setEsModificable(request.getEsModificable() != null ? request.getEsModificable() : true);

        return configuracion;
    }

    private void actualizarEntidad(ConfiguracionSucursal configuracion, ConfiguracionSucursalRequest request) {
        if (request.getValor() != null) configuracion.setValor(request.getValor());
        if (request.getDescripcion() != null) configuracion.setDescripcion(request.getDescripcion());
        if (request.getTipoDato() != null) configuracion.setTipoDato(request.getTipoDato());
        if (request.getCategoria() != null) configuracion.setCategoria(request.getCategoria());
    }

    private ConfiguracionSucursalResponse mapearRespuesta(ConfiguracionSucursal configuracion) {
        ConfiguracionSucursalResponse response = new ConfiguracionSucursalResponse();
        response.setId(configuracion.getId());
        response.setSucursalId(configuracion.getSucursal().getId());
        response.setNombreSucursal(configuracion.getSucursal().getNombre());
        response.setClave(configuracion.getClave());
        response.setValor(configuracion.getValor());
        response.setDescripcion(configuracion.getDescripcion());
        response.setTipoDato(configuracion.getTipoDato());
        response.setCategoria(configuracion.getCategoria());
        response.setEsObligatoria(configuracion.getEsObligatoria());
        response.setEsModificable(configuracion.getEsModificable());
        response.setActive(configuracion.getActive());
        response.setCreatedAt(configuracion.getCreatedAt());
        response.setUpdatedAt(configuracion.getUpdatedAt());
        response.setCreatedBy(configuracion.getCreatedBy());
        response.setUpdatedBy(configuracion.getUpdatedBy());

        return response;
    }
}

