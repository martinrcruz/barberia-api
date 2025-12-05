package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.MetodoPagoRequest;
import com.barberia.dto.MetodoPagoResponse;
import com.barberia.service.MetodoPagoService;
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
@RequestMapping("/metodos-pago")
@RequiredArgsConstructor
@Tag(name = "Métodos de Pago", description = "API para gestión de métodos de pago personalizados")
@SecurityRequirement(name = "Bearer Authentication")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un nuevo método de pago")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> crear(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoResponse metodoPago = metodoPagoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(metodoPago, "Método de pago creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un método de pago")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoResponse metodoPago = metodoPagoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(metodoPago, "Método de pago actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un método de pago (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        metodoPagoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Método de pago eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Obtener un método de pago por ID")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> obtenerPorId(@PathVariable Long id) {
        MetodoPagoResponse metodoPago = metodoPagoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(metodoPago, "Método de pago encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todos los métodos de pago con paginación")
    public ResponseEntity<ApiResponse<Page<MetodoPagoResponse>>> listarTodos(Pageable pageable) {
        Page<MetodoPagoResponse> metodosPago = metodoPagoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(metodosPago, "Métodos de pago listados exitosamente"));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Listar métodos de pago activos ordenados", 
               description = "Retorna solo métodos activos ordenados por el campo 'orden' para uso en puntos de venta")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponse>>> listarActivosOrdenados() {
        List<MetodoPagoResponse> metodosPago = metodoPagoService.listarActivosOrdenados();
        return ResponseEntity.ok(ApiResponse.success(metodosPago, "Métodos de pago activos listados exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar o desactivar un método de pago")
    public ResponseEntity<ApiResponse<MetodoPagoResponse>> activarDesactivar(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        MetodoPagoResponse metodoPago = metodoPagoService.activarDesactivar(id, activo);
        return ResponseEntity.ok(ApiResponse.success(metodoPago,
                activo ? "Método de pago activado exitosamente" : "Método de pago desactivado exitosamente"));
    }
}

