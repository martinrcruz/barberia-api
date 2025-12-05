package com.barberia.service.impl;

import com.barberia.dto.PerfilRequest;
import com.barberia.dto.PermisoResponse;
import com.barberia.dto.RolResponse;
import com.barberia.dto.ServicioFavoritoResponse;
import com.barberia.dto.SucursalBasicResponse;
import com.barberia.dto.UsuarioEstadisticasResponse;
import com.barberia.dto.UsuarioRequest;
import com.barberia.dto.UsuarioResponse;
import com.barberia.entity.DetalleVenta;
import com.barberia.entity.Permiso;
import com.barberia.entity.Rol;
import com.barberia.entity.Sucursal;
import com.barberia.entity.Usuario;
import com.barberia.entity.Venta;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.RolRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.repository.UsuarioRepository;
import com.barberia.repository.VentaRepository;
import com.barberia.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SucursalRepository sucursalRepository;
    private final VentaRepository ventaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        // Validar RUT único si se proporciona
        if (request.getRut() != null && !request.getRut().isEmpty() && 
            usuarioRepository.existsByRut(request.getRut())) {
            throw new BusinessException("El RUT ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(request.getRut());
        usuario.setDireccion(request.getDireccion());
        usuario.setNacionalidad(request.getNacionalidad());
        usuario.setFotoPerfil(request.getFotoPerfil());
        usuario.setPorcentajeComision(request.getPorcentajeComision() != null ? request.getPorcentajeComision() : 0.0);
        usuario.setActive(true);
        usuario.setCuentaBloqueada(false);

        // Encriptar contraseña si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            throw new BusinessException("La contraseña es obligatoria");
        }

        // Asignar roles
        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            Set<Rol> roles = request.getRolesIds().stream()
                    .map(rolId -> rolRepository.findById(rolId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId)))
                    .collect(Collectors.toSet());
            usuario.setRoles(roles);
        } else {
            // Asignar rol por defecto (USUARIO)
            Rol rolUsuario = rolRepository.findByCodigo("USUARIO")
                    .orElseThrow(() -> new BusinessException("Rol por defecto no encontrado"));
            usuario.setRoles(Set.of(rolUsuario));
        }

        // Asignar sucursales
        if (request.getSucursalesIds() != null && !request.getSucursalesIds().isEmpty()) {
            Set<Sucursal> sucursales = request.getSucursalesIds().stream()
                    .map(sucursalId -> sucursalRepository.findById(sucursalId)
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId)))
                    .collect(Collectors.toSet());
            usuario.setSucursales(sucursales);
        }

        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar email único si cambió
        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        // Validar RUT único si cambió
        if (request.getRut() != null && !request.getRut().isEmpty() &&
            !request.getRut().equals(usuario.getRut()) && 
            usuarioRepository.existsByRut(request.getRut())) {
            throw new BusinessException("El RUT ya está registrado");
        }

        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(request.getRut());
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getNacionalidad() != null) {
            usuario.setNacionalidad(request.getNacionalidad());
        }
        if (request.getFotoPerfil() != null) {
            usuario.setFotoPerfil(request.getFotoPerfil());
        }
        usuario.setPorcentajeComision(request.getPorcentajeComision() != null ? request.getPorcentajeComision() : usuario.getPorcentajeComision());

        // Actualizar contraseña solo si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Actualizar roles si se proporcionan
        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            Set<Rol> roles = request.getRolesIds().stream()
                    .map(rolId -> rolRepository.findById(rolId)
                            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId)))
                    .collect(Collectors.toSet());
            usuario.setRoles(roles);
        }

        // Actualizar sucursales si se proporcionan
        if (request.getSucursalesIds() != null && !request.getSucursalesIds().isEmpty()) {
            Set<Sucursal> sucursales = request.getSucursalesIds().stream()
                    .map(sucursalId -> sucursalRepository.findById(sucursalId)
                            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId)))
                    .collect(Collectors.toSet());
            usuario.setSucursales(sucursales);
        }

        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // No permitir eliminar el último admin
        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> "ADMIN".equals(rol.getCodigo()));
        
        if (esAdmin) {
            long countAdmins = usuarioRepository.findAll().stream()
                    .filter(u -> u.getRoles().stream()
                            .anyMatch(rol -> "ADMIN".equals(rol.getCodigo())))
                    .count();
            
            if (countAdmins <= 1) {
                throw new BusinessException("No se puede eliminar el último administrador");
            }
        }
        
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodosSinPaginacion() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse activarDesactivar(Long id, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        usuario.setActive(activo);
        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public void bloquearCuenta(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        usuario.setCuentaBloqueada(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desbloquearCuenta(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaBloqueo(null);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarMiPerfil(String email, PerfilRequest request) {
        Usuario usuario = usuarioRepository.findByEmailWithRolesAndPermissions(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getNacionalidad() != null) {
            usuario.setNacionalidad(request.getNacionalidad());
        }
        if (request.getFotoPerfil() != null) {
            usuario.setFotoPerfil(request.getFotoPerfil());
        }

        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioEstadisticasResponse obtenerEstadisticas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Venta> ventas = ventaRepository.findByTrabajadorId(usuarioId);

        // Calcular totales
        long totalVentas = ventas.size();
        double totalGanancia = ventas.stream()
                .mapToDouble(v -> v.getComisionTrabajador() != null ? v.getComisionTrabajador() : 0.0)
                .sum();

        // Calcular ganancia promedio mensual (últimos 6 meses)
        java.time.LocalDateTime fechaInicio = java.time.LocalDateTime.now().minusMonths(6);
        List<Venta> ventasUltimos6Meses = ventas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().isAfter(fechaInicio))
                .collect(java.util.stream.Collectors.toList());

        double gananciaUltimos6Meses = ventasUltimos6Meses.stream()
                .mapToDouble(v -> v.getComisionTrabajador() != null ? v.getComisionTrabajador() : 0.0)
                .sum();
        double gananciaPromedioMensual = ventasUltimos6Meses.isEmpty() ? 0.0 : gananciaUltimos6Meses / 6.0;

        // Calcular servicios favoritos
        java.util.Map<Long, java.util.Map<String, Object>> serviciosMap = new java.util.HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getServicio() != null) {
                        Long servicioId = detalle.getServicio().getId();
                        serviciosMap.computeIfAbsent(servicioId, k -> {
                            java.util.Map<String, Object> info = new java.util.HashMap<>();
                            info.put("servicioId", servicioId);
                            info.put("servicioNombre", detalle.getServicio().getNombre());
                            info.put("cantidadVentas", 0L);
                            return info;
                        });
                        Long cantidadActual = (Long) serviciosMap.get(servicioId).get("cantidadVentas");
                        serviciosMap.get(servicioId).put("cantidadVentas", cantidadActual + detalle.getCantidad());
                    }
                }
            }
        }

        List<ServicioFavoritoResponse> serviciosFavoritos = serviciosMap.values().stream()
                .map(info -> {
                    ServicioFavoritoResponse sf = new ServicioFavoritoResponse();
                    sf.setServicioId((Long) info.get("servicioId"));
                    sf.setServicioNombre((String) info.get("servicioNombre"));
                    sf.setCantidadVentas((Long) info.get("cantidadVentas"));
                    return sf;
                })
                .sorted((a, b) -> Long.compare(b.getCantidadVentas(), a.getCantidadVentas()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        UsuarioEstadisticasResponse response = new UsuarioEstadisticasResponse();
        response.setGananciaPromedioMensual(gananciaPromedioMensual);
        response.setServiciosFavoritos(serviciosFavoritos);
        response.setTotalVentas(totalVentas);
        response.setTotalGanancia(totalGanancia);

        return response;
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setTelefono(usuario.getTelefono());
        response.setRut(usuario.getRut());
        response.setDireccion(usuario.getDireccion());
        response.setNacionalidad(usuario.getNacionalidad());
        response.setFotoPerfil(usuario.getFotoPerfil());
        response.setPorcentajeComision(usuario.getPorcentajeComision());
        response.setCuentaBloqueada(usuario.getCuentaBloqueada() != null && usuario.getCuentaBloqueada());
        response.setActivo(usuario.getActive() != null && usuario.getActive());
        response.setCreatedAt(usuario.getCreatedAt());
        response.setUpdatedAt(usuario.getUpdatedAt());

        // Mapear roles
        Set<RolResponse> rolesResponse = usuario.getRoles().stream()
                .map(this::mapRolToResponse)
                .collect(Collectors.toSet());
        response.setRoles(rolesResponse);

        // Mapear sucursales
        if (usuario.getSucursales() != null && !usuario.getSucursales().isEmpty()) {
            Set<SucursalBasicResponse> sucursalesResponse = usuario.getSucursales().stream()
                    .map(this::mapSucursalToBasicResponse)
                    .collect(Collectors.toSet());
            response.setSucursales(sucursalesResponse);
        }

        return response;
    }

    private SucursalBasicResponse mapSucursalToBasicResponse(Sucursal sucursal) {
        SucursalBasicResponse response = new SucursalBasicResponse();
        response.setId(sucursal.getId());
        response.setNombre(sucursal.getNombre());
        response.setDireccion(sucursal.getDireccion());
        return response;
    }

    private RolResponse mapRolToResponse(Rol rol) {
        RolResponse rolResponse = new RolResponse();
        rolResponse.setId(rol.getId());
        rolResponse.setNombre(rol.getNombre());
        rolResponse.setCodigo(rol.getCodigo());
        rolResponse.setDescripcion(rol.getDescripcion());
        rolResponse.setCreatedAt(rol.getCreatedAt());
        rolResponse.setUpdatedAt(rol.getUpdatedAt());

        // Mapear permisos
        Set<PermisoResponse> permisosResponse = rol.getPermisos().stream()
                .map(this::mapPermisoToResponse)
                .collect(Collectors.toSet());
        rolResponse.setPermisos(permisosResponse);

        return rolResponse;
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

