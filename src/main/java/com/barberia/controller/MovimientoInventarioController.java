package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.MovimientoInventarioRequest;
import com.barberia.dto.MovimientoInventarioResponse;
import com.barberia.entity.MovimientoInventario;
import com.barberia.service.MovimientoInventarioService;
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
@RequestMapping("/movimientos-inventario")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Inventario", description = "API para gestión de Kardex y ajustes de inventario")
@SecurityRequirement(name = "Bearer Authentication")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @PostMapping("/ajuste")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Registrar ajuste de inventario", 
               description = "Registra un ajuste de inventario con motivo obligatorio. Los movimientos de entrada/salida por compras/ventas se registran automáticamente.")
    public ResponseEntity<ApiResponse<MovimientoInventarioResponse>> registrarAjuste(@Valid @RequestBody MovimientoInventarioRequest request) {
        MovimientoInventarioResponse movimiento = movimientoInventarioService.registrarAjuste(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movimiento, "Ajuste de inventario registrado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener movimiento de inventario por ID")
    public ResponseEntity<ApiResponse<MovimientoInventarioResponse>> obtenerPorId(@PathVariable Long id) {
        MovimientoInventarioResponse movimiento = movimientoInventarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(movimiento, "Movimiento de inventario encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los movimientos de inventario con paginación")
    public ResponseEntity<ApiResponse<Page<MovimientoInventarioResponse>>> listarTodos(Pageable pageable) {
        Page<MovimientoInventarioResponse> movimientos = movimientoInventarioService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar movimientos por sucursal")
    public ResponseEntity<ApiResponse<Page<MovimientoInventarioResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<MovimientoInventarioResponse> movimientos = movimientoInventarioService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos de la sucursal listados exitosamente"));
    }

    @GetMapping("/kardex/producto/{productoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener Kardex de un producto", 
               description = "Obtiene el historial completo de movimientos de un producto ordenado cronológicamente")
    public ResponseEntity<ApiResponse<List<MovimientoInventarioResponse>>> listarKardexPorProducto(@PathVariable Long productoId) {
        List<MovimientoInventarioResponse> kardex = movimientoInventarioService.listarKardexPorProducto(productoId);
        return ResponseEntity.ok(ApiResponse.success(kardex, "Kardex del producto obtenido exitosamente"));
    }

    @GetMapping("/kardex/insumo/{insumoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener Kardex de un insumo", 
               description = "Obtiene el historial completo de movimientos de un insumo ordenado cronológicamente")
    public ResponseEntity<ApiResponse<List<MovimientoInventarioResponse>>> listarKardexPorInsumo(@PathVariable Long insumoId) {
        List<MovimientoInventarioResponse> kardex = movimientoInventarioService.listarKardexPorInsumo(insumoId);
        return ResponseEntity.ok(ApiResponse.success(kardex, "Kardex del insumo obtenido exitosamente"));
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar movimientos por rango de fechas")
    public ResponseEntity<ApiResponse<List<MovimientoInventarioResponse>>> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<MovimientoInventarioResponse> movimientos = movimientoInventarioService.listarPorFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos listados exitosamente"));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar movimientos por tipo (ENTRADA, SALIDA, AJUSTE)")
    public ResponseEntity<ApiResponse<Page<MovimientoInventarioResponse>>> listarPorTipo(
            @PathVariable MovimientoInventario.TipoMovimiento tipoMovimiento,
            Pageable pageable) {
        Page<MovimientoInventarioResponse> movimientos = movimientoInventarioService.listarPorTipo(tipoMovimiento, pageable);
        return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos listados exitosamente"));
    }
}

