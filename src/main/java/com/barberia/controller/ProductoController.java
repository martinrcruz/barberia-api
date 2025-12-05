package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.ProductoRequest;
import com.barberia.dto.ProductoResponse;
import com.barberia.service.ProductoService;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(producto, "Producto creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar un producto")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Obtener un producto por ID")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los productos con paginación")
    public ResponseEntity<ApiResponse<Page<ProductoResponse>>> listarTodos(Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(productos, "Productos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar productos por sucursal con paginación")
    public ResponseEntity<ApiResponse<Page<ProductoResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(productos, "Productos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los productos de una sucursal sin paginación")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listarPorSucursalSinPaginacion(
            @PathVariable Long sucursalId) {
        List<ProductoResponse> productos = productoService.listarPorSucursalSinPaginacion(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(productos, "Productos listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar productos con stock bajo")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listarConStockBajo(@PathVariable Long sucursalId) {
        List<ProductoResponse> productos = productoService.listarConStockBajo(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(productos, "Productos con stock bajo listados"));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar stock de un producto (ENTRADA/SALIDA)")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            @RequestParam String tipo) {
        ProductoResponse producto = productoService.actualizarStock(id, cantidad, tipo);
        return ResponseEntity.ok(ApiResponse.success(producto, "Stock actualizado exitosamente"));
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Buscar producto por código")
    public ResponseEntity<ApiResponse<ProductoResponse>> buscarPorCodigo(@PathVariable String codigo) {
        ProductoResponse producto = productoService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto encontrado"));
    }
}

