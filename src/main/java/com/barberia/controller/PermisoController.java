package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.PermisoRequest;
import com.barberia.dto.PermisoResponse;
import com.barberia.entity.Permiso;
import com.barberia.service.PermisoService;
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
@RequestMapping("/permisos")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "API para gestión de permisos del sistema")
@SecurityRequirement(name = "Bearer Authentication")
public class PermisoController {

    private final PermisoService permisoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo permiso")
    public ResponseEntity<ApiResponse<PermisoResponse>> crear(@Valid @RequestBody PermisoRequest request) {
        PermisoResponse permiso = permisoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(permiso, "Permiso creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un permiso")
    public ResponseEntity<ApiResponse<PermisoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PermisoRequest request) {
        PermisoResponse permiso = permisoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(permiso, "Permiso actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un permiso (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        permisoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Permiso eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener un permiso por ID")
    public ResponseEntity<ApiResponse<PermisoResponse>> obtenerPorId(@PathVariable Long id) {
        PermisoResponse permiso = permisoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(permiso, "Permiso encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los permisos con paginación")
    public ResponseEntity<ApiResponse<Page<PermisoResponse>>> listarTodos(Pageable pageable) {
        Page<PermisoResponse> permisos = permisoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(permisos, "Permisos listados exitosamente"));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los permisos sin paginación")
    public ResponseEntity<ApiResponse<List<PermisoResponse>>> listarTodosSinPaginacion() {
        List<PermisoResponse> permisos = permisoService.listarTodosSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(permisos, "Permisos listados exitosamente"));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar permisos por tipo")
    public ResponseEntity<ApiResponse<List<PermisoResponse>>> listarPorTipo(@PathVariable Permiso.TipoPermiso tipo) {
        List<PermisoResponse> permisos = permisoService.listarPorTipo(tipo);
        return ResponseEntity.ok(ApiResponse.success(permisos, "Permisos listados por tipo exitosamente"));
    }
}

