package com.barberia.controller;

import com.barberia.dto.ConfiguracionSucursalRequest;
import com.barberia.dto.ConfiguracionSucursalResponse;
import com.barberia.service.ConfiguracionSucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configuracion-sucursal")
@RequiredArgsConstructor
@Tag(name = "Configuración Sucursal", description = "API para gestionar la configuración específica de cada sucursal")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConfiguracionSucursalController {

    private final ConfiguracionSucursalService configuracionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CONFIGURACION_CREAR')")
    @Operation(summary = "Crear nueva configuración de sucursal")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody ConfiguracionSucursalRequest request) {
        ConfiguracionSucursalResponse response = configuracionService.crear(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuración creada exitosamente");
        result.put("data", response);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CONFIGURACION_EDITAR')")
    @Operation(summary = "Actualizar configuración de sucursal")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracionSucursalRequest request) {
        ConfiguracionSucursalResponse response = configuracionService.actualizar(id, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuración actualizada exitosamente");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuración por ID")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        ConfiguracionSucursalResponse response = configuracionService.obtenerPorId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener todas las configuraciones de una sucursal")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursal(@PathVariable Long sucursalId) {
        List<ConfiguracionSucursalResponse> response = configuracionService.obtenerPorSucursal(sucursalId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/paginated")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuraciones de una sucursal con paginación")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursalPaginado(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "categoria") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ConfiguracionSucursalResponse> response = configuracionService.obtenerPorSucursalPaginado(sucursalId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/clave/{clave}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuración específica por sucursal y clave")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursalYClave(
            @PathVariable Long sucursalId,
            @PathVariable String clave) {
        ConfiguracionSucursalResponse response = configuracionService.obtenerPorSucursalYClave(sucursalId, clave);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/categoria/{categoria}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuraciones por sucursal y categoría")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursalYCategoria(
            @PathVariable Long sucursalId,
            @PathVariable String categoria) {
        List<ConfiguracionSucursalResponse> response = configuracionService.obtenerPorSucursalYCategoria(sucursalId, categoria);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar configuraciones por palabra clave")
    public ResponseEntity<Map<String, Object>> buscarPorPalabraClave(
            @PathVariable Long sucursalId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConfiguracionSucursalResponse> response = configuracionService.buscarPorPalabraClave(sucursalId, keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CONFIGURACION_ELIMINAR')")
    @Operation(summary = "Eliminar configuración")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        configuracionService.eliminar(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuración eliminada exitosamente");

        return ResponseEntity.ok(result);
    }

    @PutMapping("/sucursal/{sucursalId}/clave/{clave}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CONFIGURACION_EDITAR')")
    @Operation(summary = "Crear o actualizar configuración específica")
    public ResponseEntity<Map<String, Object>> crearOActualizar(
            @PathVariable Long sucursalId,
            @PathVariable String clave,
            @RequestBody Map<String, String> body) {
        String valor = body.get("valor");
        ConfiguracionSucursalResponse response = configuracionService.crearOActualizar(sucursalId, clave, valor);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuración guardada exitosamente");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/sucursal/{sucursalId}/inicializar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inicializar configuraciones por defecto para una sucursal")
    public ResponseEntity<Map<String, Object>> inicializarConfiguracionesPorDefecto(@PathVariable Long sucursalId) {
        configuracionService.inicializarConfiguracionesPorDefecto(sucursalId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuraciones por defecto inicializadas exitosamente");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/mapa")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuración de sucursal como mapa clave-valor")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracionComoMapa(@PathVariable Long sucursalId) {
        Map<String, String> configuracion = configuracionService.obtenerConfiguracionComoMapa(sucursalId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", configuracion);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/obligatorias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener configuraciones obligatorias de una sucursal")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracionesObligatorias(@PathVariable Long sucursalId) {
        List<ConfiguracionSucursalResponse> response = configuracionService.obtenerConfiguracionesObligatorias(sucursalId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}

