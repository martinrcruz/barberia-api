package com.barberia.service.impl;

import com.barberia.dto.PersonalizacionVisualRequest;
import com.barberia.dto.PersonalizacionVisualResponse;
import com.barberia.entity.PersonalizacionVisual;
import com.barberia.entity.Sucursal;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.PersonalizacionVisualRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.PersonalizacionVisualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PersonalizacionVisualServiceImpl implements PersonalizacionVisualService {

    private final PersonalizacionVisualRepository personalizacionRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    public PersonalizacionVisualResponse crear(PersonalizacionVisualRequest request) {
        log.info("Creando nueva personalización visual");

        // Validar que no exista ya una configuración global si se está creando una
        if (Boolean.TRUE.equals(request.getEsGlobal()) && personalizacionRepository.existsByEsGlobalTrueAndActiveTrue()) {
            throw new BusinessException("Ya existe una configuración global de personalización visual");
        }

        // Validar que no exista ya para la sucursal
        if (request.getSucursalId() != null && personalizacionRepository.existsBySucursalIdAndActiveTrue(request.getSucursalId())) {
            throw new BusinessException("Ya existe una configuración de personalización para esta sucursal");
        }

        PersonalizacionVisual personalizacion = mapearEntidad(request);
        PersonalizacionVisual guardada = personalizacionRepository.save(personalizacion);
        log.info("Personalización visual creada exitosamente con ID: {}", guardada.getId());

        return mapearRespuesta(guardada);
    }

    @Override
    public PersonalizacionVisualResponse actualizar(Long id, PersonalizacionVisualRequest request) {
        log.info("Actualizando personalización visual con ID: {}", id);

        PersonalizacionVisual personalizacion = personalizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Personalización visual no encontrada con ID: " + id));

        actualizarEntidad(personalizacion, request);
        PersonalizacionVisual actualizada = personalizacionRepository.save(personalizacion);
        log.info("Personalización visual actualizada exitosamente");

        return mapearRespuesta(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalizacionVisualResponse obtenerPorId(Long id) {
        log.info("Obteniendo personalización visual con ID: {}", id);

        PersonalizacionVisual personalizacion = personalizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Personalización visual no encontrada con ID: " + id));

        return mapearRespuesta(personalizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalizacionVisualResponse obtenerGlobal() {
        log.info("Obteniendo personalización visual global");

        return personalizacionRepository.findPersonalizacionGlobal()
            .map(this::mapearRespuesta)
            .orElse(crearPersonalizacionPorDefecto());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalizacionVisualResponse obtenerPorSucursal(Long sucursalId) {
        log.info("Obteniendo personalización visual para sucursal ID: {}", sucursalId);

        return personalizacionRepository.findBySucursalId(sucursalId)
            .map(this::mapearRespuesta)
            .orElseGet(this::obtenerGlobal); // Si no existe para la sucursal, devolver la global
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalizacionVisualResponse> obtenerTodas() {
        log.info("Obteniendo todas las personalizaciones visuales");

        return personalizacionRepository.findAll().stream()
            .map(this::mapearRespuesta)
            .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando personalización visual con ID: {}", id);

        PersonalizacionVisual personalizacion = personalizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Personalización visual no encontrada con ID: " + id));

        personalizacion.setActive(false);
        personalizacionRepository.save(personalizacion);
        log.info("Personalización visual eliminada (desactivada) exitosamente");
    }

    @Override
    public PersonalizacionVisualResponse crearOActualizarGlobal(PersonalizacionVisualRequest request) {
        log.info("Creando o actualizando personalización visual global");

        return personalizacionRepository.findPersonalizacionGlobal()
            .map(existente -> {
                actualizarEntidad(existente, request);
                return mapearRespuesta(personalizacionRepository.save(existente));
            })
            .orElseGet(() -> {
                request.setEsGlobal(true);
                request.setSucursalId(null);
                return crear(request);
            });
    }

    @Override
    public PersonalizacionVisualResponse crearOActualizarPorSucursal(Long sucursalId, PersonalizacionVisualRequest request) {
        log.info("Creando o actualizando personalización visual para sucursal ID: {}", sucursalId);

        return personalizacionRepository.findBySucursalId(sucursalId)
            .map(existente -> {
                actualizarEntidad(existente, request);
                return mapearRespuesta(personalizacionRepository.save(existente));
            })
            .orElseGet(() -> {
                request.setSucursalId(sucursalId);
                request.setEsGlobal(false);
                return crear(request);
            });
    }

    // Métodos auxiliares

    private PersonalizacionVisual mapearEntidad(PersonalizacionVisualRequest request) {
        PersonalizacionVisual personalizacion = new PersonalizacionVisual();

        if (request.getSucursalId() != null && !Boolean.TRUE.equals(request.getEsGlobal())) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
            personalizacion.setSucursal(sucursal);
        }

        personalizacion.setLogoUrl(request.getLogoUrl());
        personalizacion.setFaviconUrl(request.getFaviconUrl());
        personalizacion.setColorPrimario(request.getColorPrimario() != null ? request.getColorPrimario() : "#08415C");
        personalizacion.setColorSecundario(request.getColorSecundario() != null ? request.getColorSecundario() : "#EF6461");
        personalizacion.setColorAcento(request.getColorAcento() != null ? request.getColorAcento() : "#D5DFE5");
        personalizacion.setColorTexto(request.getColorTexto() != null ? request.getColorTexto() : "#FFFFFF");
        personalizacion.setDarkModeHabilitado(request.getDarkModeHabilitado() != null ? request.getDarkModeHabilitado() : false);
        personalizacion.setNombreEmpresa(request.getNombreEmpresa());
        personalizacion.setEslogan(request.getEslogan());
        personalizacion.setTelefonoContacto(request.getTelefonoContacto());
        personalizacion.setEmailContacto(request.getEmailContacto());
        personalizacion.setSitioWeb(request.getSitioWeb());
        personalizacion.setDireccion(request.getDireccion());
        personalizacion.setMostrarLogoEnComprobantes(request.getMostrarLogoEnComprobantes() != null ? request.getMostrarLogoEnComprobantes() : true);
        personalizacion.setPieDePaginaComprobante(request.getPieDePaginaComprobante());
        personalizacion.setMensajeBienvenida(request.getMensajeBienvenida());
        personalizacion.setEsGlobal(request.getEsGlobal() != null ? request.getEsGlobal() : false);

        return personalizacion;
    }

    private void actualizarEntidad(PersonalizacionVisual personalizacion, PersonalizacionVisualRequest request) {
        if (request.getSucursalId() != null && !Boolean.TRUE.equals(request.getEsGlobal())) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
            personalizacion.setSucursal(sucursal);
        }

        if (request.getLogoUrl() != null) personalizacion.setLogoUrl(request.getLogoUrl());
        if (request.getFaviconUrl() != null) personalizacion.setFaviconUrl(request.getFaviconUrl());
        if (request.getColorPrimario() != null) personalizacion.setColorPrimario(request.getColorPrimario());
        if (request.getColorSecundario() != null) personalizacion.setColorSecundario(request.getColorSecundario());
        if (request.getColorAcento() != null) personalizacion.setColorAcento(request.getColorAcento());
        if (request.getColorTexto() != null) personalizacion.setColorTexto(request.getColorTexto());
        if (request.getDarkModeHabilitado() != null) personalizacion.setDarkModeHabilitado(request.getDarkModeHabilitado());
        if (request.getNombreEmpresa() != null) personalizacion.setNombreEmpresa(request.getNombreEmpresa());
        if (request.getEslogan() != null) personalizacion.setEslogan(request.getEslogan());
        if (request.getTelefonoContacto() != null) personalizacion.setTelefonoContacto(request.getTelefonoContacto());
        if (request.getEmailContacto() != null) personalizacion.setEmailContacto(request.getEmailContacto());
        if (request.getSitioWeb() != null) personalizacion.setSitioWeb(request.getSitioWeb());
        if (request.getDireccion() != null) personalizacion.setDireccion(request.getDireccion());
        if (request.getMostrarLogoEnComprobantes() != null) personalizacion.setMostrarLogoEnComprobantes(request.getMostrarLogoEnComprobantes());
        if (request.getPieDePaginaComprobante() != null) personalizacion.setPieDePaginaComprobante(request.getPieDePaginaComprobante());
        if (request.getMensajeBienvenida() != null) personalizacion.setMensajeBienvenida(request.getMensajeBienvenida());
    }

    private PersonalizacionVisualResponse mapearRespuesta(PersonalizacionVisual personalizacion) {
        PersonalizacionVisualResponse response = new PersonalizacionVisualResponse();
        response.setId(personalizacion.getId());

        if (personalizacion.getSucursal() != null) {
            response.setSucursalId(personalizacion.getSucursal().getId());
            response.setNombreSucursal(personalizacion.getSucursal().getNombre());
        }

        response.setLogoUrl(personalizacion.getLogoUrl());
        response.setFaviconUrl(personalizacion.getFaviconUrl());
        response.setColorPrimario(personalizacion.getColorPrimario());
        response.setColorSecundario(personalizacion.getColorSecundario());
        response.setColorAcento(personalizacion.getColorAcento());
        response.setColorTexto(personalizacion.getColorTexto());
        response.setDarkModeHabilitado(personalizacion.getDarkModeHabilitado());
        response.setNombreEmpresa(personalizacion.getNombreEmpresa());
        response.setEslogan(personalizacion.getEslogan());
        response.setTelefonoContacto(personalizacion.getTelefonoContacto());
        response.setEmailContacto(personalizacion.getEmailContacto());
        response.setSitioWeb(personalizacion.getSitioWeb());
        response.setDireccion(personalizacion.getDireccion());
        response.setMostrarLogoEnComprobantes(personalizacion.getMostrarLogoEnComprobantes());
        response.setPieDePaginaComprobante(personalizacion.getPieDePaginaComprobante());
        response.setMensajeBienvenida(personalizacion.getMensajeBienvenida());
        response.setEsGlobal(personalizacion.getEsGlobal());
        response.setActive(personalizacion.getActive());
        response.setCreatedAt(personalizacion.getCreatedAt());
        response.setUpdatedAt(personalizacion.getUpdatedAt());
        response.setCreatedBy(personalizacion.getCreatedBy());
        response.setUpdatedBy(personalizacion.getUpdatedBy());

        return response;
    }

    private PersonalizacionVisualResponse crearPersonalizacionPorDefecto() {
        PersonalizacionVisualResponse response = new PersonalizacionVisualResponse();
        response.setColorPrimario("#08415C");
        response.setColorSecundario("#EF6461");
        response.setColorAcento("#D5DFE5");
        response.setColorTexto("#FFFFFF");
        response.setDarkModeHabilitado(false);
        response.setMostrarLogoEnComprobantes(true);
        response.setEsGlobal(true);
        response.setActive(true);
        return response;
    }
}

