package com.barberia.service.impl;

import com.barberia.dto.AuthResponse;
import com.barberia.dto.LoginRequest;
import com.barberia.dto.RegisterRequest;
import com.barberia.entity.Permiso;
import com.barberia.entity.Rol;
import com.barberia.entity.Usuario;
import com.barberia.exception.BusinessException;
import com.barberia.repository.RolRepository;
import com.barberia.repository.UsuarioRepository;
import com.barberia.service.AuthService;
import com.barberia.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailWithRolesAndPermissions(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        // Verificar si la cuenta está bloqueada
        if (usuario.getCuentaBloqueada() != null && usuario.getCuentaBloqueada()) {
            if (usuario.getFechaBloqueo() != null &&
                    LocalDateTime.now().isBefore(usuario.getFechaBloqueo().plusMinutes(15))) {
                throw new BusinessException("Cuenta bloqueada. Intente más tarde.");
            } else {
                // Desbloquear cuenta si pasó el tiempo
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Resetear intentos fallidos en login exitoso
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuario.setCuentaBloqueada(false);
                usuarioRepository.save(usuario);
            }

            return generateAuthResponse(usuario);

        } catch (Exception e) {
            // Incrementar intentos fallidos
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

            if (usuario.getIntentosFallidos() >= maxLoginAttempts) {
                usuario.setCuentaBloqueada(true);
                usuario.setFechaBloqueo(LocalDateTime.now());
            }

            usuarioRepository.save(usuario);
            throw new BusinessException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        if (request.getRut() != null && usuarioRepository.existsByRut(request.getRut())) {
            throw new BusinessException("El RUT ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(request.getRut());
        usuario.setActive(true);

        // Asignar rol por defecto (USUARIO)
        Rol rolUsuario = rolRepository.findByCodigo("USUARIO")
                .orElseThrow(() -> new BusinessException("Rol por defecto no encontrado"));
        usuario.setRoles(Set.of(rolUsuario));

        usuario = usuarioRepository.save(usuario);

        return generateAuthResponse(usuario);
    }

    @Override
    public void logout(String token) {
        // Implementar lógica de blacklist de tokens si es necesario
        log.info("Usuario deslogueado");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByEmailWithRolesAndPermissions(userEmail)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new BusinessException("Token inválido");
        }

        return generateAuthResponse(usuario);
    }

    @Override
    @Transactional
    public void solicitarRecuperacionPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(24));

        usuarioRepository.save(usuario);

        // TODO: Enviar email con token de recuperación
        log.info("Token de recuperación generado para: {}", email);
    }

    @Override
    @Transactional
    public void recuperarPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new BusinessException("Token inválido"));

        if (usuario.getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token expirado");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaExpiracionToken(null);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);

        usuarioRepository.save(usuario);
    }

    private AuthResponse generateAuthResponse(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getCodigo)
                .collect(Collectors.toSet());

        Set<String> permisos = new HashSet<>();
        for (Rol rol : usuario.getRoles()) {
            permisos.addAll(rol.getPermisos().stream()
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toSet()));
        }

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .roles(roles)
                .permisos(permisos)
                .build();
    }
}

