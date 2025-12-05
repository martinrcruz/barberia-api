package com.barberia.service.impl;

import com.barberia.dto.PermisoRequest;
import com.barberia.dto.PermisoResponse;
import com.barberia.entity.Permiso;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.PermisoRepository;
import com.barberia.service.PermisoService;
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
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;

    @Override
    @Transactional
    public PermisoResponse crear(PermisoRequest request) {
        log.info("Creando nuevo permiso con código: {}", request.getCodigo());
        
        // Validar que no exista un permiso con el mismo código
        if (permisoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new BusinessException("Ya existe un permiso con el código: " + request.getCodigo());
        }

        // Validar que no exista un permiso con el mismo nombre
        if (permisoRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new BusinessException("Ya existe un permiso con el nombre: " + request.getNombre());
        }

        Permiso permiso = new Permiso();
        permiso.setNombre(request.getNombre());
        permiso.setCodigo(request.getCodigo());
        permiso.setDescripcion(request.getDescripcion());
        permiso.setTipo(request.getTipo());
        permiso.setRecurso(request.getRecurso());
        permiso.setActive(true);

        permiso = permisoRepository.save(permiso);
        log.info("Permiso creado exitosamente con ID: {}", permiso.getId());

        return mapToResponse(permiso);
    }

    @Override
    @Transactional
    public PermisoResponse actualizar(Long id, PermisoRequest request) {
        log.info("Actualizando permiso con ID: {}", id);

        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));

        // Validar que no exista otro permiso con el mismo código
        permisoRepository.findByCodigo(request.getCodigo()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new BusinessException("Ya existe otro permiso con el código: " + request.getCodigo());
            }
        });

        // Validar que no exista otro permiso con el mismo nombre
        permisoRepository.findByNombre(request.getNombre()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new BusinessException("Ya existe otro permiso con el nombre: " + request.getNombre());
            }
        });

        permiso.setNombre(request.getNombre());
        permiso.setCodigo(request.getCodigo());
        permiso.setDescripcion(request.getDescripcion());
        permiso.setTipo(request.getTipo());
        permiso.setRecurso(request.getRecurso());

        permiso = permisoRepository.save(permiso);
        log.info("Permiso actualizado exitosamente con ID: {}", id);

        return mapToResponse(permiso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando permiso con ID: {}", id);

        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));

        // Borrado lógico
        permiso.setActive(false);
        permisoRepository.save(permiso);
        log.info("Permiso eliminado exitosamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoResponse obtenerPorId(Long id) {
        log.info("Obteniendo permiso con ID: {}", id);

        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));

        return mapToResponse(permiso);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermisoResponse> listarTodos(Pageable pageable) {
        log.info("Listando todos los permisos con paginación");
        return permisoRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarTodosSinPaginacion() {
        log.info("Listando todos los permisos sin paginación");
        return permisoRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponse> listarPorTipo(Permiso.TipoPermiso tipo) {
        log.info("Listando permisos por tipo: {}", tipo);
        return permisoRepository.findByTipoAndActiveTrue(tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PermisoResponse mapToResponse(Permiso permiso) {
        PermisoResponse response = new PermisoResponse();
        response.setId(permiso.getId());
        response.setNombre(permiso.getNombre());
        response.setCodigo(permiso.getCodigo());
        response.setDescripcion(permiso.getDescripcion());
        response.setTipo(permiso.getTipo());
        response.setRecurso(permiso.getRecurso());
        response.setCreatedAt(permiso.getCreatedAt());
        response.setUpdatedAt(permiso.getUpdatedAt());
        return response;
    }
}

