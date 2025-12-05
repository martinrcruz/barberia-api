package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.ProveedorRequest;
import com.barberia.dto.ProveedorResponse;
import com.barberia.service.ProveedorService;
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
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "API para gestión de proveedores")
@SecurityRequirement(name = "Bearer Authentication")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear un nuevo proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponse>> crear(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedor = proveedorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(proveedor, "Proveedor creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar un proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedor = proveedorService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(proveedor, "Proveedor actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar un proveedor (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Proveedor eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener un proveedor por ID")
    public ResponseEntity<ApiResponse<ProveedorResponse>> obtenerPorId(@PathVariable Long id) {
        ProveedorResponse proveedor = proveedorService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(proveedor, "Proveedor encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los proveedores con paginación")
    public ResponseEntity<ApiResponse<Page<ProveedorResponse>>> listarTodos(Pageable pageable) {
        Page<ProveedorResponse> proveedores = proveedorService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(proveedores, "Proveedores listados exitosamente"));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los proveedores sin paginación")
    public ResponseEntity<ApiResponse<List<ProveedorResponse>>> listarTodosSinPaginacion() {
        List<ProveedorResponse> proveedores = proveedorService.listarTodosSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(proveedores, "Proveedores listados exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Activar o desactivar un proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponse>> activarDesactivar(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        ProveedorResponse proveedor = proveedorService.activarDesactivar(id, activo);
        return ResponseEntity.ok(ApiResponse.success(proveedor,
                activo ? "Proveedor activado exitosamente" : "Proveedor desactivado exitosamente"));
    }
}

