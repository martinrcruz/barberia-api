package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.VarianteProductoRequest;
import com.barberia.dto.VarianteProductoResponse;
import com.barberia.service.VarianteProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variantes-producto")
@RequiredArgsConstructor
@Tag(name = "Variantes de Producto", description = "API para gestión de variantes de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class VarianteProductoController {

    private final VarianteProductoService varianteProductoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear una nueva variante de producto")
    public ResponseEntity<ApiResponse<VarianteProductoResponse>> crear(@Valid @RequestBody VarianteProductoRequest request) {
        VarianteProductoResponse variante = varianteProductoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(variante, "Variante de producto creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar una variante de producto")
    public ResponseEntity<ApiResponse<VarianteProductoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VarianteProductoRequest request) {
        VarianteProductoResponse variante = varianteProductoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(variante, "Variante de producto actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar una variante de producto (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        varianteProductoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Variante de producto eliminada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Obtener una variante de producto por ID")
    public ResponseEntity<ApiResponse<VarianteProductoResponse>> obtenerPorId(@PathVariable Long id) {
        VarianteProductoResponse variante = varianteProductoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(variante, "Variante de producto encontrada"));
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Listar variantes de un producto específico",
               description = "Retorna todas las variantes activas de un producto ordenadas por campo 'orden'")
    public ResponseEntity<ApiResponse<List<VarianteProductoResponse>>> listarPorProducto(@PathVariable Long productoId) {
        List<VarianteProductoResponse> variantes = varianteProductoService.listarPorProducto(productoId);
        return ResponseEntity.ok(ApiResponse.success(variantes, "Variantes listadas exitosamente"));
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Obtener variante por SKU")
    public ResponseEntity<ApiResponse<VarianteProductoResponse>> obtenerPorSku(@PathVariable String sku) {
        VarianteProductoResponse variante = varianteProductoService.obtenerPorSku(sku);
        return ResponseEntity.ok(ApiResponse.success(variante, "Variante encontrada"));
    }
}

