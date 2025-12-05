package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.RegistroJornadaResponse;
import com.barberia.service.RegistroJornadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/jornadas")
@RequiredArgsConstructor
@Tag(name = "Jornadas Laborales", description = "API para gestión de jornadas laborales (check-in/check-out)")
@SecurityRequirement(name = "Bearer Authentication")
public class RegistroJornadaController {

    private final RegistroJornadaService registroJornadaService;

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'TRABAJADOR')")
    @Operation(summary = "Registrar entrada (check-in)")
    public ResponseEntity<ApiResponse<RegistroJornadaResponse>> registrarEntrada(
            @RequestParam Long usuarioId,
            @RequestParam Long sucursalId,
            @RequestParam(required = false) String observaciones) {
        RegistroJornadaResponse jornada = registroJornadaService.registrarEntrada(usuarioId, sucursalId, observaciones);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(jornada, "Entrada registrada exitosamente"));
    }

    @PostMapping("/salida")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'TRABAJADOR')")
    @Operation(summary = "Registrar salida (check-out)")
    public ResponseEntity<ApiResponse<RegistroJornadaResponse>> registrarSalida(
            @RequestParam Long usuarioId,
            @RequestParam Long sucursalId,
            @RequestParam(required = false) String observaciones) {
        RegistroJornadaResponse jornada = registroJornadaService.registrarSalida(usuarioId, sucursalId, observaciones);
        return ResponseEntity.ok(ApiResponse.success(jornada, "Salida registrada exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener jornada por ID")
    public ResponseEntity<ApiResponse<RegistroJornadaResponse>> obtenerPorId(@PathVariable Long id) {
        RegistroJornadaResponse jornada = registroJornadaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(jornada, "Jornada encontrada"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar todas las jornadas con paginación")
    public ResponseEntity<ApiResponse<Page<RegistroJornadaResponse>>> listarTodos(Pageable pageable) {
        Page<RegistroJornadaResponse> jornadas = registroJornadaService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas listadas exitosamente"));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'TRABAJADOR')")
    @Operation(summary = "Listar jornadas de un usuario específico")
    public ResponseEntity<ApiResponse<Page<RegistroJornadaResponse>>> listarPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        Page<RegistroJornadaResponse> jornadas = registroJornadaService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas del usuario listadas exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar jornadas de una sucursal específica")
    public ResponseEntity<ApiResponse<Page<RegistroJornadaResponse>>> listarPorSucursal(
            @PathVariable Long sucursalId,
            Pageable pageable) {
        Page<RegistroJornadaResponse> jornadas = registroJornadaService.listarPorSucursal(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas de la sucursal listadas exitosamente"));
    }

    @GetMapping("/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar jornadas por rango de fechas")
    public ResponseEntity<ApiResponse<Page<RegistroJornadaResponse>>> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Pageable pageable) {
        Page<RegistroJornadaResponse> jornadas = registroJornadaService.listarPorFechas(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas listadas exitosamente"));
    }

    @GetMapping("/usuario/{usuarioId}/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'TRABAJADOR')")
    @Operation(summary = "Listar jornadas de un usuario por rango de fechas")
    public ResponseEntity<ApiResponse<List<RegistroJornadaResponse>>> listarPorUsuarioYFechas(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<RegistroJornadaResponse> jornadas = registroJornadaService.listarPorUsuarioYFechas(usuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas del usuario listadas exitosamente"));
    }

    @GetMapping("/sucursal/{sucursalId}/fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar jornadas de una sucursal por rango de fechas")
    public ResponseEntity<ApiResponse<List<RegistroJornadaResponse>>> listarPorSucursalYFechas(
            @PathVariable Long sucursalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<RegistroJornadaResponse> jornadas = registroJornadaService.listarPorSucursalYFechas(sucursalId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(jornadas, "Jornadas de la sucursal listadas exitosamente"));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Cancelar una jornada activa")
    public ResponseEntity<ApiResponse<Void>> cancelarJornada(
            @PathVariable Long id,
            @RequestParam String motivo) {
        registroJornadaService.cancelarJornada(id, motivo);
        return ResponseEntity.ok(ApiResponse.success(null, "Jornada cancelada exitosamente"));
    }
}

