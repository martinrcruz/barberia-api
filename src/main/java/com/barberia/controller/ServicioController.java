package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.ServicioRequest;
import com.barberia.dto.ServicioResponse;
import com.barberia.service.ServicioService;
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
@RequestMapping("/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "API para gestión de servicios")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicioController {

    private final ServicioService servicioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear un nuevo servicio")
    public ResponseEntity<ApiResponse<ServicioResponse>> crear(@Valid @RequestBody ServicioRequest request) {
        ServicioResponse servicio = servicioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(servicio, "Servicio creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar un servicio")
    public ResponseEntity<ApiResponse<ServicioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequest request) {
        ServicioResponse servicio = servicioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(servicio, "Servicio actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Eliminar un servicio")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Servicio eliminado exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Obtener un servicio por ID")
    public ResponseEntity<ApiResponse<ServicioResponse>> obtenerPorId(@PathVariable Long id) {
        ServicioResponse servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(servicio, "Servicio encontrado"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los servicios con paginación")
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> listarTodos(Pageable pageable) {
        Page<ServicioResponse> servicios = servicioService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(servicios, "Servicios listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar servicios por sucursal con paginación")
    public ResponseEntity<ApiResponse<Page<ServicioResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<ServicioResponse> servicios = servicioService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(servicios, "Servicios listados exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar todos los servicios de una sucursal sin paginación")
    public ResponseEntity<ApiResponse<List<ServicioResponse>>> listarPorSucursalSinPaginacion(
            @PathVariable Long sucursalId) {
        List<ServicioResponse> servicios = servicioService.listarPorSucursalSinPaginacion(sucursalId);
        return ResponseEntity.ok(ApiResponse.success(servicios, "Servicios listados exitosamente"));
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Buscar servicio por código")
    public ResponseEntity<ApiResponse<ServicioResponse>> buscarPorCodigo(@PathVariable String codigo) {
        ServicioResponse servicio = servicioService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(ApiResponse.success(servicio, "Servicio encontrado"));
    }
}

