package com.barberia.controller;

import com.barberia.dto.PersonalizacionVisualRequest;
import com.barberia.dto.PersonalizacionVisualResponse;
import com.barberia.service.PersonalizacionVisualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personalizacion-visual")
@RequiredArgsConstructor
@Tag(name = "Personalización Visual", description = "API para gestionar la personalización visual de la plataforma")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PersonalizacionVisualController {

    private final PersonalizacionVisualService personalizacionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERSONALIZACION_CREAR')")
    @Operation(summary = "Crear nueva personalización visual")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody PersonalizacionVisualRequest request) {
        PersonalizacionVisualResponse response = personalizacionService.crear(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Personalización visual creada exitosamente");
        result.put("data", response);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERSONALIZACION_EDITAR')")
    @Operation(summary = "Actualizar personalización visual")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PersonalizacionVisualRequest request) {
        PersonalizacionVisualResponse response = personalizacionService.actualizar(id, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Personalización visual actualizada exitosamente");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener personalización visual por ID")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        PersonalizacionVisualResponse response = personalizacionService.obtenerPorId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/global")
    @Operation(summary = "Obtener personalización visual global (pública)")
    public ResponseEntity<Map<String, Object>> obtenerGlobal() {
        PersonalizacionVisualResponse response = personalizacionService.obtenerGlobal();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener personalización visual por sucursal")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursal(@PathVariable Long sucursalId) {
        PersonalizacionVisualResponse response = personalizacionService.obtenerPorSucursal(sucursalId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todas las personalizaciones visuales")
    public ResponseEntity<Map<String, Object>> obtenerTodas() {
        List<PersonalizacionVisualResponse> response = personalizacionService.obtenerTodas();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERSONALIZACION_ELIMINAR')")
    @Operation(summary = "Eliminar personalización visual")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        personalizacionService.eliminar(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Personalización visual eliminada exitosamente");

        return ResponseEntity.ok(result);
    }

    @PutMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear o actualizar personalización visual global")
    public ResponseEntity<Map<String, Object>> crearOActualizarGlobal(
            @Valid @RequestBody PersonalizacionVisualRequest request) {
        PersonalizacionVisualResponse response = personalizacionService.crearOActualizarGlobal(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Personalización visual global guardada exitosamente");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERSONALIZACION_EDITAR')")
    @Operation(summary = "Crear o actualizar personalización visual por sucursal")
    public ResponseEntity<Map<String, Object>> crearOActualizarPorSucursal(
            @PathVariable Long sucursalId,
            @Valid @RequestBody PersonalizacionVisualRequest request) {
        PersonalizacionVisualResponse response = personalizacionService.crearOActualizarPorSucursal(sucursalId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Personalización visual de la sucursal guardada exitosamente");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }
}

