package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.CompraRequest;
import com.barberia.dto.CompraResponse;
import com.barberia.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "API para gestión de compras a proveedores")
@SecurityRequirement(name = "Bearer Authentication")
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Registrar una nueva compra", 
               description = "Registra una compra, actualiza el stock de productos/insumos y genera movimientos en el Kardex")
    public ResponseEntity<ApiResponse<CompraResponse>> registrarCompra(@Valid @RequestBody CompraRequest request) {
        CompraResponse compra = compraService.registrarCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(compra, "Compra registrada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener compra por ID")
    public ResponseEntity<ApiResponse<CompraResponse>> obtenerPorId(@PathVariable Long id) {
        CompraResponse compra = compraService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(compra, "Compra encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todas las compras con paginación")
    public ResponseEntity<ApiResponse<Page<CompraResponse>>> listarTodos(Pageable pageable) {
        Page<CompraResponse> compras = compraService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(compras, "Compras listadas exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar compras por sucursal")
    public ResponseEntity<ApiResponse<Page<CompraResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<CompraResponse> compras = compraService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(compras, "Compras de la sucursal listadas exitosamente"));
    }

    @GetMapping("/proveedor/{proveedorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar compras por proveedor")
    public ResponseEntity<ApiResponse<Page<CompraResponse>>> listarPorProveedor(
            @PathVariable Long proveedorId,
            Pageable pageable) {
        Page<CompraResponse> compras = compraService.listarPorProveedor(proveedorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(compras, "Compras del proveedor listadas exitosamente"));
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar compras por rango de fechas")
    public ResponseEntity<ApiResponse<List<CompraResponse>>> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<CompraResponse> compras = compraService.listarPorFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(compras, "Compras listadas exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar compras por sucursal y rango de fechas")
    public ResponseEntity<ApiResponse<List<CompraResponse>>> listarPorSucursalYFechas(
            @PathVariable Long sucursalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<CompraResponse> compras = compraService.listarPorSucursalYFechas(sucursalId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(compras, "Compras de la sucursal listadas exitosamente"));
    }
}

