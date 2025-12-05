package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.InsumoRequest;
import com.barberia.dto.InsumoResponse;
import com.barberia.service.InsumoService;
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
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
@Tag(name = "Insumos", description = "API para gestión de insumos")
@SecurityRequirement(name = "Bearer Authentication")
public class InsumoController {

    private final InsumoService insumoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear un nuevo insumo")
    public ResponseEntity<ApiResponse<InsumoResponse>> crear(@Valid @RequestBody InsumoRequest request) {
        InsumoResponse insumo = insumoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(insumo, "Insumo creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar un insumo")
    public ResponseEntity<ApiResponse<InsumoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InsumoRequest request) {
        InsumoResponse insumo = insumoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(insumo, "Insumo actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar un insumo")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        insumoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Insumo eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Obtener un insumo por ID")
    public ResponseEntity<ApiResponse<InsumoResponse>> obtenerPorId(@PathVariable Long id) {
        InsumoResponse insumo = insumoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(insumo, "Insumo encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los insumos con paginación")
    public ResponseEntity<ApiResponse<Page<InsumoResponse>>> listarTodos(Pageable pageable) {
        Page<InsumoResponse> insumos = insumoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(insumos, "Insumos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar insumos por sucursal con paginación")
    public ResponseEntity<ApiResponse<Page<InsumoResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<InsumoResponse> insumos = insumoService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(insumos, "Insumos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los insumos de una sucursal sin paginación")
    public ResponseEntity<ApiResponse<List<InsumoResponse>>> listarPorSucursalSinPaginacion(
            @PathVariable Long sucursalId) {
        List<InsumoResponse> insumos = insumoService.listarPorSucursalSinPaginacion(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(insumos, "Insumos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar insumos con stock bajo")
    public ResponseEntity<ApiResponse<List<InsumoResponse>>> listarConStockBajo(@PathVariable Long sucursalId) {
        List<InsumoResponse> insumos = insumoService.listarConStockBajo(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(insumos, "Insumos con stock bajo listados"));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar stock de un insumo (ENTRADA/SALIDA)")
    public ResponseEntity<ApiResponse<InsumoResponse>> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            @RequestParam String tipo) {
        InsumoResponse insumo = insumoService.actualizarStock(id, cantidad, tipo);
        return ResponseEntity.ok(ApiResponse.success(insumo, "Stock actualizado exitosamente"));
    }
}

