package com.barberia.service.impl;

import com.barberia.dto.SucursalRequest;
import com.barberia.dto.SucursalResponse;
import com.barberia.dto.UsuarioBasicResponse;
import com.barberia.entity.Sucursal;
import com.barberia.entity.Usuario;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.SucursalRepository;
import com.barberia.repository.UsuarioRepository;
import com.barberia.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public SucursalResponse crear(SucursalRequest request) {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());
        sucursal.setHorarioApertura(request.getHorarioApertura());
        sucursal.setHorarioCierre(request.getHorarioCierre());
        sucursal.setDiasAtencion(request.getDiasAtencion());
        sucursal.setComisionDefecto(request.getComisionDefecto());

        if (request.getAdministradorId() != null) {
            Usuario administrador = usuarioRepository.findById(request.getAdministradorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario administrador no encontrado"));
            sucursal.setAdministrador(administrador);
        }

        sucursal = sucursalRepository.save(sucursal);
        return mapToResponse(sucursal);
    }

    @Override
    @Transactional
    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setEmail(request.getEmail());
        sucursal.setHorarioApertura(request.getHorarioApertura());
        sucursal.setHorarioCierre(request.getHorarioCierre());
        sucursal.setDiasAtencion(request.getDiasAtencion());
        sucursal.setComisionDefecto(request.getComisionDefecto());

        if (request.getAdministradorId() != null) {
            Usuario administrador = usuarioRepository.findById(request.getAdministradorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario administrador no encontrado"));
            sucursal.setAdministrador(administrador);
        } else {
            sucursal.setAdministrador(null);
        }

        sucursal = sucursalRepository.save(sucursal);
        return mapToResponse(sucursal);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada");
        }
        sucursalRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        return mapToResponse(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalResponse> listarTodos(Pageable pageable) {
        return sucursalRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> listarTodasSinPaginacion() {
        return sucursalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SucursalResponse mapToResponse(Sucursal sucursal) {
        SucursalResponse response = new SucursalResponse();
        response.setId(sucursal.getId());
        response.setNombre(sucursal.getNombre());
        response.setDireccion(sucursal.getDireccion());
        response.setTelefono(sucursal.getTelefono());
        response.setEmail(sucursal.getEmail());
        response.setHorarioApertura(sucursal.getHorarioApertura());
        response.setHorarioCierre(sucursal.getHorarioCierre());
        response.setDiasAtencion(sucursal.getDiasAtencion());
        response.setComisionDefecto(sucursal.getComisionDefecto());
        response.setCreatedAt(sucursal.getCreatedAt());
        response.setUpdatedAt(sucursal.getUpdatedAt());

        if (sucursal.getAdministrador() != null) {
            UsuarioBasicResponse adminResponse = new UsuarioBasicResponse();
            adminResponse.setId(sucursal.getAdministrador().getId());
            adminResponse.setNombre(sucursal.getAdministrador().getNombre());
            adminResponse.setEmail(sucursal.getAdministrador().getEmail());
            response.setAdministrador(adminResponse);
        }

        return response;
    }
}

