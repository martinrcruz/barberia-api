package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.CategoriaRequest;
import com.barberia.dto.CategoriaResponse;
import com.barberia.entity.Categoria;
import com.barberia.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "API para gestión de categorías")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear una nueva categoría")
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoria, "Categoría creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar una categoría")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoria = categoriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(categoria, "Categoría actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar una categoría")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Categoría eliminada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Obtener una categoría por ID")
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        CategoriaResponse categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(categoria, "Categoría encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todas las categorías con paginación")
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> listarTodos(Pageable pageable) {
        Page<CategoriaResponse> categorias = categoriaService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(categorias, "Categorías listadas exitosamente"));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todas las categorías sin paginación")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listarTodasSinPaginacion() {
        List<CategoriaResponse> categorias = categoriaService.listarTodasSinPaginacion();
        return ResponseEntity.ok(ApiResponse.success(categorias, "Categorías listadas exitosamente"));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar categorías por tipo")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listarPorTipo(
            @PathVariable Categoria.TipoCategoria tipo) {
        List<CategoriaResponse> categorias = categoriaService.listarPorTipo(tipo);
        return ResponseEntity.ok(ApiResponse.success(categorias, "Categorías listadas exitosamente"));
    }
}

