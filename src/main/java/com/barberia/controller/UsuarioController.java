package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.UsuarioRequest;
import com.barberia.dto.UsuarioResponse;
import com.barberia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(usuario, "Usuario creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un usuario")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener un usuario por ID")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los usuarios con paginación")
    public ResponseEntity<ApiResponse<Page<UsuarioResponse>>> listarTodos(Pageable pageable) {
        Page<UsuarioResponse> usuarios = usuarioService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Usuarios listados exitosamente"));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los usuarios sin paginación")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listarTodasSinPaginacion() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodosSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Usuarios listados exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar o desactivar un usuario")
    public ResponseEntity<ApiResponse<UsuarioResponse>> activarDesactivar(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        UsuarioResponse usuario = usuarioService.activarDesactivar(id, activo);
        return ResponseEntity.ok(ApiResponse.success(usuario, 
                activo ? "Usuario activado exitosamente" : "Usuario desactivado exitosamente"));
    }

    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloquear cuenta de usuario")
    public ResponseEntity<ApiResponse<Void>> bloquearCuenta(@PathVariable Long id) {
        usuarioService.bloquearCuenta(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cuenta bloqueada exitosamente"));
    }

    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desbloquear cuenta de usuario")
    public ResponseEntity<ApiResponse<Void>> desbloquearCuenta(@PathVariable Long id) {
        usuarioService.desbloquearCuenta(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cuenta desbloqueada exitosamente"));
    }

    @PutMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar mi perfil")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarMiPerfil(Authentication authentication,
                                                                           @Valid @RequestBody com.barberia.dto.PerfilRequest request) {
        String email = authentication.getName();
        UsuarioResponse usuario = usuarioService.actualizarMiPerfil(email, request);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Perfil actualizado exitosamente"));
    }

    @GetMapping("/{id}/estadisticas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estadísticas del usuario")
    public ResponseEntity<ApiResponse<com.barberia.dto.UsuarioEstadisticasResponse>> obtenerEstadisticas(
            @PathVariable Long id) {
        com.barberia.dto.UsuarioEstadisticasResponse estadisticas = usuarioService.obtenerEstadisticas(id);
        return ResponseEntity.ok(ApiResponse.success(estadisticas, "Estadísticas obtenidas exitosamente"));
    }
}

