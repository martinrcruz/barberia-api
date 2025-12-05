package com.barberia.service.impl;

import com.barberia.dto.RegistroJornadaRequest;
import com.barberia.dto.RegistroJornadaResponse;
import com.barberia.dto.SucursalBasicResponse;
import com.barberia.dto.UsuarioBasicResponse;
import com.barberia.entity.RegistroJornada;
import com.barberia.entity.Sucursal;
import com.barberia.entity.Usuario;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.RegistroJornadaRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.repository.UsuarioRepository;
import com.barberia.service.RegistroJornadaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroJornadaServiceImpl implements RegistroJornadaService {

    private final RegistroJornadaRepository registroJornadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    @Transactional
    public RegistroJornadaResponse registrarEntrada(Long usuarioId, Long sucursalId, String observaciones) {
        log.info("Registrando entrada del usuario ID: {} en sucursal ID: {}", usuarioId, sucursalId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));

        // Verificar que el usuario no tenga una jornada activa en la sucursal
        Optional<RegistroJornada> jornadaActiva = registroJornadaRepository
                .findByUsuarioIdAndSucursalIdAndEstado(usuarioId, sucursalId, RegistroJornada.EstadoJornada.ACTIVA);

        if (jornadaActiva.isPresent()) {
            throw new BusinessException("El usuario ya tiene una jornada activa en esta sucursal. Debe registrar salida primero.");
        }

        // Verificar que el usuario esté asignado a la sucursal
        boolean usuarioAsignadoASucursal = usuario.getSucursales().stream()
                .anyMatch(s -> s.getId().equals(sucursalId));

        if (!usuarioAsignadoASucursal) {
            throw new BusinessException("El usuario no está asignado a esta sucursal");
        }

        RegistroJornada jornada = new RegistroJornada();
        jornada.setUsuario(usuario);
        jornada.setSucursal(sucursal);
        jornada.setFechaEntrada(LocalDateTime.now());
        jornada.setEstado(RegistroJornada.EstadoJornada.ACTIVA);
        jornada.setObservaciones(observaciones);
        jornada.setActive(true);

        jornada = registroJornadaRepository.save(jornada);
        log.info("Entrada registrada exitosamente. Jornada ID: {}", jornada.getId());

        return mapToResponse(jornada);
    }

    @Override
    @Transactional
    public RegistroJornadaResponse registrarSalida(Long usuarioId, Long sucursalId, String observaciones) {
        log.info("Registrando salida del usuario ID: {} en sucursal ID: {}", usuarioId, sucursalId);

        // Buscar jornada activa
        RegistroJornada jornada = registroJornadaRepository
                .findByUsuarioIdAndSucursalIdAndEstado(usuarioId, sucursalId, RegistroJornada.EstadoJornada.ACTIVA)
                .orElseThrow(() -> new BusinessException("No se encontró una jornada activa para este usuario en esta sucursal"));

        jornada.setFechaSalida(LocalDateTime.now());
        jornada.calcularHorasTrabajadas();
        jornada.setEstado(RegistroJornada.EstadoJornada.FINALIZADA);
        
        if (observaciones != null && !observaciones.isEmpty()) {
            String obsExistentes = jornada.getObservaciones() != null ? jornada.getObservaciones() + " | " : "";
            jornada.setObservaciones(obsExistentes + observaciones);
        }

        jornada = registroJornadaRepository.save(jornada);
        log.info("Salida registrada exitosamente. Jornada ID: {}. Horas trabajadas: {}", 
                 jornada.getId(), jornada.getHorasTrabajadas());

        return mapToResponse(jornada);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroJornadaResponse obtenerPorId(Long id) {
        log.info("Obteniendo jornada con ID: {}", id);
        RegistroJornada jornada = registroJornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con ID: " + id));
        return mapToResponse(jornada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroJornadaResponse> listarTodos(Pageable pageable) {
        log.info("Listando todas las jornadas con paginación");
        return registroJornadaRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroJornadaResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        log.info("Listando jornadas del usuario ID: {}", usuarioId);
        return registroJornadaRepository.findByUsuarioIdOrderByFechaEntradaDesc(usuarioId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroJornadaResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        log.info("Listando jornadas de la sucursal ID: {}", sucursalId);
        return registroJornadaRepository.findBySucursalIdOrderByFechaEntradaDesc(sucursalId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistroJornadaResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        log.info("Listando jornadas entre {} y {}", fechaInicio, fechaFin);
        return registroJornadaRepository.findByFechaEntradaBetween(fechaInicio, fechaFin, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroJornadaResponse> listarPorUsuarioYFechas(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando jornadas del usuario ID: {} entre {} y {}", usuarioId, fechaInicio, fechaFin);
        return registroJornadaRepository.findByUsuarioIdAndFechaEntradaBetween(usuarioId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroJornadaResponse> listarPorSucursalYFechas(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando jornadas de la sucursal ID: {} entre {} y {}", sucursalId, fechaInicio, fechaFin);
        return registroJornadaRepository.findBySucursalIdAndFechaEntradaBetween(sucursalId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarJornada(Long id, String motivo) {
        log.info("Cancelando jornada ID: {}. Motivo: {}", id, motivo);
        
        RegistroJornada jornada = registroJornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con ID: " + id));

        if (jornada.getEstado() != RegistroJornada.EstadoJornada.ACTIVA) {
            throw new BusinessException("Solo se pueden cancelar jornadas activas");
        }

        jornada.setEstado(RegistroJornada.EstadoJornada.CANCELADA);
        String obsExistentes = jornada.getObservaciones() != null ? jornada.getObservaciones() + " | " : "";
        jornada.setObservaciones(obsExistentes + "CANCELADA: " + motivo);

        registroJornadaRepository.save(jornada);
        log.info("Jornada cancelada exitosamente. ID: {}", id);
    }

    private RegistroJornadaResponse mapToResponse(RegistroJornada jornada) {
        RegistroJornadaResponse response = new RegistroJornadaResponse();
        response.setId(jornada.getId());
        response.setFechaEntrada(jornada.getFechaEntrada());
        response.setFechaSalida(jornada.getFechaSalida());
        response.setHorasTrabajadas(jornada.getHorasTrabajadas());
        response.setObservaciones(jornada.getObservaciones());
        response.setEstado(jornada.getEstado());
        response.setCreatedAt(jornada.getCreatedAt());
        response.setUpdatedAt(jornada.getUpdatedAt());

        // Mapear usuario
        UsuarioBasicResponse usuarioResponse = new UsuarioBasicResponse();
        usuarioResponse.setId(jornada.getUsuario().getId());
        usuarioResponse.setNombre(jornada.getUsuario().getNombre());
        usuarioResponse.setApellido(jornada.getUsuario().getApellido());
        usuarioResponse.setEmail(jornada.getUsuario().getEmail());
        response.setUsuario(usuarioResponse);

        // Mapear sucursal
        SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
        sucursalResponse.setId(jornada.getSucursal().getId());
        sucursalResponse.setNombre(jornada.getSucursal().getNombre());
        sucursalResponse.setDireccion(jornada.getSucursal().getDireccion());
        response.setSucursal(sucursalResponse);

        return response;
    }
}

