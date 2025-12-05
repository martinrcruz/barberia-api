package com.barberia.service.impl;

import com.barberia.dto.PermisoResponse;
import com.barberia.dto.RolRequest;
import com.barberia.dto.RolResponse;
import com.barberia.entity.Permiso;
import com.barberia.entity.Rol;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.PermisoRepository;
import com.barberia.repository.RolRepository;
import com.barberia.service.RolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    @Override
    @Transactional
    public RolResponse crear(RolRequest request) {
        if (rolRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un rol con el código especificado");
        }
        if (rolRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un rol con el nombre especificado");
        }

        Rol rol = new Rol();
        rol.setNombre(request.getNombre());
        rol.setCodigo(request.getCodigo());
        rol.setDescripcion(request.getDescripcion());

        rol = rolRepository.save(rol);
        return mapToResponse(rol);
    }

    @Override
    @Transactional
    public RolResponse actualizar(Long id, RolRequest request) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        if (!rol.getCodigo().equals(request.getCodigo()) && rolRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un rol con el código especificado");
        }
        if (!rol.getNombre().equals(request.getNombre()) && rolRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un rol con el nombre especificado");
        }

        rol.setNombre(request.getNombre());
        rol.setCodigo(request.getCodigo());
        rol.setDescripcion(request.getDescripcion());

        rol = rolRepository.save(rol);
        return mapToResponse(rol);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        // No permitir eliminar rol si está en uso podría añadirse aquí
        rolRepository.delete(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponse obtenerPorId(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        return mapToResponse(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolResponse> listarTodos(Pageable pageable) {
        return rolRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> listarTodosSinPaginacion() {
        return rolRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RolResponse clonar(Long id, String nuevoNombre, String nuevoCodigo) {
        log.info("Clonando rol con ID: {} al nuevo rol: {} ({})", id, nuevoNombre, nuevoCodigo);
        
        Rol rolOriginal = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));

        if (rolRepository.existsByCodigo(nuevoCodigo)) {
            throw new BusinessException("Ya existe un rol con el código: " + nuevoCodigo);
        }
        if (rolRepository.existsByNombre(nuevoNombre)) {
            throw new BusinessException("Ya existe un rol con el nombre: " + nuevoNombre);
        }

        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nuevoNombre);
        nuevoRol.setCodigo(nuevoCodigo);
        nuevoRol.setDescripcion("Clonado de: " + rolOriginal.getNombre() + ". " + 
                                 (rolOriginal.getDescripcion() != null ? rolOriginal.getDescripcion() : ""));
        nuevoRol.setActive(true);
        
        // Copiar todos los permisos del rol original
        Set<Permiso> permisosCopiados = new HashSet<>(rolOriginal.getPermisos());
        nuevoRol.setPermisos(permisosCopiados);

        nuevoRol = rolRepository.save(nuevoRol);
        log.info("Rol clonado exitosamente. Nuevo rol ID: {}", nuevoRol.getId());
        
        return mapToResponse(nuevoRol);
    }

    @Override
    @Transactional
    public RolResponse agregarPermisos(Long rolId, List<Long> permisosIds) {
        log.info("Agregando {} permisos al rol ID: {}", permisosIds.size(), rolId);
        
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        if (permisosIds == null || permisosIds.isEmpty()) {
            throw new BusinessException("Debe proporcionar al menos un permiso para agregar");
        }

        List<Permiso> permisos = permisoRepository.findAllById(permisosIds);
        
        if (permisos.size() != permisosIds.size()) {
            throw new ResourceNotFoundException("Algunos permisos no fueron encontrados");
        }

        rol.getPermisos().addAll(permisos);
        rol = rolRepository.save(rol);
        
        log.info("Permisos agregados exitosamente al rol ID: {}", rolId);
        return mapToResponse(rol);
    }

    @Override
    @Transactional
    public RolResponse removerPermisos(Long rolId, List<Long> permisosIds) {
        log.info("Removiendo {} permisos del rol ID: {}", permisosIds.size(), rolId);
        
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        if (permisosIds == null || permisosIds.isEmpty()) {
            throw new BusinessException("Debe proporcionar al menos un permiso para remover");
        }

        List<Permiso> permisos = permisoRepository.findAllById(permisosIds);
        rol.getPermisos().removeAll(permisos);
        rol = rolRepository.save(rol);
        
        log.info("Permisos removidos exitosamente del rol ID: {}", rolId);
        return mapToResponse(rol);
    }

    private RolResponse mapToResponse(Rol rol) {
        RolResponse response = new RolResponse();
        response.setId(rol.getId());
        response.setNombre(rol.getNombre());
        response.setCodigo(rol.getCodigo());
        response.setDescripcion(rol.getDescripcion());
        response.setCreatedAt(rol.getCreatedAt());
        response.setUpdatedAt(rol.getUpdatedAt());

        if (rol.getPermisos() != null) {
            Set<PermisoResponse> permisosResponse = rol.getPermisos().stream()
                    .map(this::mapPermisoToResponse)
                    .collect(Collectors.toSet());
            response.setPermisos(permisosResponse);
        }

        return response;
    }

    private PermisoResponse mapPermisoToResponse(Permiso permiso) {
        PermisoResponse permisoResponse = new PermisoResponse();
        permisoResponse.setId(permiso.getId());
        permisoResponse.setNombre(permiso.getNombre());
        permisoResponse.setCodigo(permiso.getCodigo());
        permisoResponse.setDescripcion(permiso.getDescripcion());
        permisoResponse.setTipo(permiso.getTipo());
        if (permiso.getRecurso() != null) {
            permisoResponse.setRecurso(permiso.getRecurso());
        }
        permisoResponse.setCreatedAt(permiso.getCreatedAt());
        permisoResponse.setUpdatedAt(permiso.getUpdatedAt());
        return permisoResponse;
    }
}


