package com.barberia.service.impl;

import com.barberia.dto.MetodoPagoRequest;
import com.barberia.dto.MetodoPagoResponse;
import com.barberia.entity.MetodoPago;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.MetodoPagoRepository;
import com.barberia.service.MetodoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    @Transactional
    public MetodoPagoResponse crear(MetodoPagoRequest request) {
        log.info("Creando nuevo método de pago: {}", request.getNombre());

        // Validar código único
        if (metodoPagoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un método de pago con el código: " + request.getCodigo());
        }

        // Validar nombre único
        if (metodoPagoRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un método de pago con el nombre: " + request.getNombre());
        }

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre(request.getNombre());
        metodoPago.setCodigo(request.getCodigo());
        metodoPago.setDescripcion(request.getDescripcion());
        metodoPago.setEsElectronico(request.getEsElectronico() != null ? request.getEsElectronico() : false);
        metodoPago.setRequiereReferencia(request.getRequiereReferencia() != null ? request.getRequiereReferencia() : false);
        metodoPago.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        metodoPago.setIcono(request.getIcono());
        metodoPago.setTipoMetodo(request.getTipoMetodo());
        metodoPago.setActive(true);

        metodoPago = metodoPagoRepository.save(metodoPago);
        log.info("Método de pago creado exitosamente con ID: {}", metodoPago.getId());

        return mapToResponse(metodoPago);
    }

    @Override
    @Transactional
    public MetodoPagoResponse actualizar(Long id, MetodoPagoRequest request) {
        log.info("Actualizando método de pago con ID: {}", id);

        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + id));

        // Validar código único
        metodoPagoRepository.findByCodigo(request.getCodigo()).ifPresent(mp -> {
            if (!mp.getId().equals(id)) {
                throw new BusinessException("Ya existe otro método de pago con el código: " + request.getCodigo());
            }
        });

        // Validar nombre único
        metodoPagoRepository.findByNombre(request.getNombre()).ifPresent(mp -> {
            if (!mp.getId().equals(id)) {
                throw new BusinessException("Ya existe otro método de pago con el nombre: " + request.getNombre());
            }
        });

        metodoPago.setNombre(request.getNombre());
        metodoPago.setCodigo(request.getCodigo());
        metodoPago.setDescripcion(request.getDescripcion());
        metodoPago.setEsElectronico(request.getEsElectronico() != null ? request.getEsElectronico() : false);
        metodoPago.setRequiereReferencia(request.getRequiereReferencia() != null ? request.getRequiereReferencia() : false);
        metodoPago.setOrden(request.getOrden() != null ? request.getOrden() : metodoPago.getOrden());
        metodoPago.setIcono(request.getIcono());
        metodoPago.setTipoMetodo(request.getTipoMetodo());

        metodoPago = metodoPagoRepository.save(metodoPago);
        log.info("Método de pago actualizado exitosamente con ID: {}", id);

        return mapToResponse(metodoPago);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando método de pago con ID: {}", id);

        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + id));

        // Borrado lógico
        metodoPago.setActive(false);
        metodoPagoRepository.save(metodoPago);
        log.info("Método de pago eliminado exitosamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagoResponse obtenerPorId(Long id) {
        log.info("Obteniendo método de pago con ID: {}", id);

        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + id));

        return mapToResponse(metodoPago);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MetodoPagoResponse> listarTodos(Pageable pageable) {
        log.info("Listando todos los métodos de pago con paginación");
        return metodoPagoRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarActivosOrdenados() {
        log.info("Listando métodos de pago activos ordenados");
        return metodoPagoRepository.findByActiveTrueOrderByOrdenAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MetodoPagoResponse activarDesactivar(Long id, Boolean activo) {
        log.info("{} método de pago con ID: {}", activo ? "Activando" : "Desactivando", id);

        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + id));

        metodoPago.setActive(activo);
        metodoPago = metodoPagoRepository.save(metodoPago);

        log.info("Método de pago {} exitosamente. ID: {}", activo ? "activado" : "desactivado", id);
        return mapToResponse(metodoPago);
    }

    private MetodoPagoResponse mapToResponse(MetodoPago metodoPago) {
        MetodoPagoResponse response = new MetodoPagoResponse();
        response.setId(metodoPago.getId());
        response.setNombre(metodoPago.getNombre());
        response.setCodigo(metodoPago.getCodigo());
        response.setDescripcion(metodoPago.getDescripcion());
        response.setEsElectronico(metodoPago.getEsElectronico());
        response.setRequiereReferencia(metodoPago.getRequiereReferencia());
        response.setOrden(metodoPago.getOrden());
        response.setIcono(metodoPago.getIcono());
        response.setTipoMetodo(metodoPago.getTipoMetodo());
        response.setActivo(metodoPago.getActive() != null && metodoPago.getActive());
        response.setCreatedAt(metodoPago.getCreatedAt());
        response.setUpdatedAt(metodoPago.getUpdatedAt());
        return response;
    }
}

