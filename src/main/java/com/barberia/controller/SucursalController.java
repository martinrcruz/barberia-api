package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.SucursalRequest;
import com.barberia.dto.SucursalResponse;
import com.barberia.service.SucursalService;
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
@RequestMapping("/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "API para gestión de sucursales")
@SecurityRequirement(name = "Bearer Authentication")
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva sucursal")
    public ResponseEntity<ApiResponse<SucursalResponse>> crear(@Valid @RequestBody SucursalRequest request) {
        SucursalResponse sucursal = sucursalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sucursal, "Sucursal creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una sucursal")
    public ResponseEntity<ApiResponse<SucursalResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SucursalRequest request) {
        SucursalResponse sucursal = sucursalService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(sucursal, "Sucursal actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una sucursal")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Sucursal eliminada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Obtener una sucursal por ID")
    public ResponseEntity<ApiResponse<SucursalResponse>> obtenerPorId(@PathVariable Long id) {
        SucursalResponse sucursal = sucursalService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(sucursal, "Sucursal encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todas las sucursales con paginación")
    public ResponseEntity<ApiResponse<Page<SucursalResponse>>> listarTodos(Pageable pageable) {
        Page<SucursalResponse> sucursales = sucursalService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(sucursales, "Sucursales listadas exitosamente"));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todas las sucursales sin paginación")
    public ResponseEntity<ApiResponse<List<SucursalResponse>>> listarTodasSinPaginacion() {
        List<SucursalResponse> sucursales = sucursalService.listarTodasSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(sucursales, "Sucursales listadas exitosamente"));
    }
}

