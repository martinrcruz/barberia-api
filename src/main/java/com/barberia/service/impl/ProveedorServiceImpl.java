package com.barberia.service.impl;

import com.barberia.dto.ProveedorRequest;
import com.barberia.dto.ProveedorResponse;
import com.barberia.entity.Proveedor;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.ProveedorRepository;
import com.barberia.service.ProveedorService;
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
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        log.info("Creando nuevo proveedor con RUT: {}", request.getRut());

        // Validar que no exista un proveedor con el mismo RUT
        if (proveedorRepository.existsByRut(request.getRut())) {
            throw new BusinessException("Ya existe un proveedor con el RUT: " + request.getRut());
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRut(request.getRut());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setNombreFantasia(request.getNombreFantasia());
        proveedor.setEmail(request.getEmail());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setContactoNombre(request.getContactoNombre());
        proveedor.setContactoTelefono(request.getContactoTelefono());
        proveedor.setObservaciones(request.getObservaciones());
        proveedor.setActive(true);

        proveedor = proveedorRepository.save(proveedor);
        log.info("Proveedor creado exitosamente con ID: {}", proveedor.getId());

        return mapToResponse(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        log.info("Actualizando proveedor con ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        // Validar que no exista otro proveedor con el mismo RUT
        proveedorRepository.findByRut(request.getRut()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new BusinessException("Ya existe otro proveedor con el RUT: " + request.getRut());
            }
        });

        proveedor.setRut(request.getRut());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setNombreFantasia(request.getNombreFantasia());
        proveedor.setEmail(request.getEmail());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setContactoNombre(request.getContactoNombre());
        proveedor.setContactoTelefono(request.getContactoTelefono());
        proveedor.setObservaciones(request.getObservaciones());

        proveedor = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado exitosamente con ID: {}", id);

        return mapToResponse(proveedor);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando proveedor con ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        // Borrado lógico
        proveedor.setActive(false);
        proveedorRepository.save(proveedor);
        log.info("Proveedor eliminado exitosamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse obtenerPorId(Long id) {
        log.info("Obteniendo proveedor con ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        return mapToResponse(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponse> listarTodos(Pageable pageable) {
        log.info("Listando todos los proveedores con paginación");
        return proveedorRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarTodosSinPaginacion() {
        log.info("Listando todos los proveedores sin paginación");
        return proveedorRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProveedorResponse activarDesactivar(Long id, Boolean activo) {
        log.info("{} proveedor con ID: {}", activo ? "Activando" : "Desactivando", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        proveedor.setActive(activo);
        proveedor = proveedorRepository.save(proveedor);

        log.info("Proveedor {} exitosamente. ID: {}", activo ? "activado" : "desactivado", id);
        return mapToResponse(proveedor);
    }

    private ProveedorResponse mapToResponse(Proveedor proveedor) {
        ProveedorResponse response = new ProveedorResponse();
        response.setId(proveedor.getId());
        response.setRut(proveedor.getRut());
        response.setRazonSocial(proveedor.getRazonSocial());
        response.setNombreFantasia(proveedor.getNombreFantasia());
        response.setEmail(proveedor.getEmail());
        response.setTelefono(proveedor.getTelefono());
        response.setDireccion(proveedor.getDireccion());
        response.setContactoNombre(proveedor.getContactoNombre());
        response.setContactoTelefono(proveedor.getContactoTelefono());
        response.setObservaciones(proveedor.getObservaciones());
        response.setActivo(proveedor.getActive() != null && proveedor.getActive());
        response.setCreatedAt(proveedor.getCreatedAt());
        response.setUpdatedAt(proveedor.getUpdatedAt());
        return response;
    }
}

