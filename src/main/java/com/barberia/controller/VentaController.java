package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.VentaRequest;
import com.barberia.dto.VentaResponse;
import com.barberia.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Gestión de ventas")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    @PreAuthorize("hasAuthority('VENTA_CREAR')")
    @Operation(summary = "Crear nueva venta")
    public ResponseEntity<ApiResponse<VentaResponse>> crearVenta(@Valid @RequestBody VentaRequest request) {
        VentaResponse response = ventaService.crearVenta(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Venta creada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTA_VER')")
    @Operation(summary = "Obtener venta por ID")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerVenta(@PathVariable Long id) {
        VentaResponse response = ventaService.obtenerVentaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VENTA_VER')")
    @Operation(summary = "Listar todas las ventas con paginación")
    public ResponseEntity<ApiResponse<Page<VentaResponse>>> listarVentas(Pageable pageable) {
        Page<VentaResponse> response = ventaService.listarVentas(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAuthority('VENTA_VER')")
    @Operation(summary = "Listar ventas por sucursal")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listarVentasPorSucursal(@PathVariable Long sucursalId) {
        List<VentaResponse> response = ventaService.listarVentasPorSucursal(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasAuthority('VENTA_VER')")
    @Operation(summary = "Listar ventas por rango de fechas")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listarVentasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        List<VentaResponse> response = ventaService.listarVentasPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTA_ELIMINAR')")
    @Operation(summary = "Anular venta")
    public ResponseEntity<ApiResponse<Void>> anularVenta(@PathVariable Long id) {
        ventaService.anularVenta(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Venta anulada exitosamente"));
    }

    @GetMapping("/{id}/comprobante")
    @PreAuthorize("hasAuthority('VENTA_VER')")
    @Operation(summary = "Generar comprobante de venta")
    public ResponseEntity<byte[]> generarComprobante(@PathVariable Long id) {
        byte[] comprobante = ventaService.generarComprobante(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=comprobante-" + id + ".pdf")
                .body(comprobante);
    }
}

