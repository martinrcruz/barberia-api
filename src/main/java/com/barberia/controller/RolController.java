package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.RolRequest;
import com.barberia.dto.RolResponse;
import com.barberia.service.RolService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para gestión de roles")
@SecurityRequirement(name = "Bearer Authentication")
public class RolController {

    private final RolService rolService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo rol")
    public ResponseEntity<ApiResponse<RolResponse>> crear(@Valid @RequestBody RolRequest request) {
        RolResponse rol = rolService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rol, "Rol creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un rol")
    public ResponseEntity<ApiResponse<RolResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RolRequest request) {
        RolResponse rol = rolService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(rol, "Rol actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un rol")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rol eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener un rol por ID")
    public ResponseEntity<ApiResponse<RolResponse>> obtenerPorId(@PathVariable Long id) {
        RolResponse rol = rolService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(rol, "Rol encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los roles con paginación")
    public ResponseEntity<ApiResponse<Page<RolResponse>>> listarTodos(Pageable pageable) {
        Page<RolResponse> roles = rolService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles listados exitosamente"));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los roles sin paginación")
    public ResponseEntity<ApiResponse<List<RolResponse>>> listarTodasSinPaginacion() {
        List<RolResponse> roles = rolService.listarTodosSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(roles, "Roles listados exitosamente"));
    }

    @PostMapping("/{id}/clonar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clonar un rol existente con todos sus permisos")
    public ResponseEntity<ApiResponse<RolResponse>> clonar(
            @PathVariable Long id,
            @RequestParam String nuevoNombre,
            @RequestParam String nuevoCodigo) {
        RolResponse rol = rolService.clonar(id, nuevoNombre, nuevoCodigo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rol, "Rol clonado exitosamente"));
    }

    @PostMapping("/{id}/permisos/agregar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar permisos a un rol")
    public ResponseEntity<ApiResponse<RolResponse>> agregarPermisos(
            @PathVariable Long id,
            @RequestBody List<Long> permisosIds) {
        RolResponse rol = rolService.agregarPermisos(id, permisosIds);
        return ResponseEntity.ok(ApiResponse.success(rol, "Permisos agregados exitosamente"));
    }

    @PostMapping("/{id}/permisos/remover")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover permisos de un rol")
    public ResponseEntity<ApiResponse<RolResponse>> removerPermisos(
            @PathVariable Long id,
            @RequestBody List<Long> permisosIds) {
        RolResponse rol = rolService.removerPermisos(id, permisosIds);
        return ResponseEntity.ok(ApiResponse.success(rol, "Permisos removidos exitosamente"));
    }
}


