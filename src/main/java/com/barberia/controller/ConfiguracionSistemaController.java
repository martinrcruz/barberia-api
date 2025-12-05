package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.ConfiguracionSistemaRequest;
import com.barberia.dto.ConfiguracionSistemaResponse;
import com.barberia.service.ConfiguracionSistemaService;
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
@RequestMapping("/configuraciones")
@RequiredArgsConstructor
@Tag(name = "Configuraciones", description = "Gestión de configuración del sistema")
@SecurityRequirement(name = "bearerAuth")
public class ConfiguracionSistemaController {

    private final ConfiguracionSistemaService configuracionSistemaService;

    @GetMapping
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Listar todas las configuraciones")
    public ResponseEntity<ApiResponse<List<ConfiguracionSistemaResponse>>> listarTodas() {
        List<ConfiguracionSistemaResponse> configs = configuracionSistemaService.listarTodas();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/editables")
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Listar configuraciones editables")
    public ResponseEntity<ApiResponse<List<ConfiguracionSistemaResponse>>> listarEditables() {
        List<ConfiguracionSistemaResponse> configs = configuracionSistemaService.listarEditables();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Listar configuraciones por categoría")
    public ResponseEntity<ApiResponse<List<ConfiguracionSistemaResponse>>> listarPorCategoria(
            @PathVariable String categoria) {
        List<ConfiguracionSistemaResponse> configs = configuracionSistemaService.listarPorCategoria(categoria);
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Obtener configuración por ID")
    public ResponseEntity<ApiResponse<ConfiguracionSistemaResponse>> obtenerPorId(@PathVariable Long id) {
        ConfiguracionSistemaResponse config = configuracionSistemaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Crear nueva configuración")
    public ResponseEntity<ApiResponse<ConfiguracionSistemaResponse>> crear(
            @Valid @RequestBody ConfiguracionSistemaRequest request) {
        ConfiguracionSistemaResponse config = configuracionSistemaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(config, "Configuración creada exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Actualizar configuración")
    public ResponseEntity<ApiResponse<ConfiguracionSistemaResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracionSistemaRequest request) {
        ConfiguracionSistemaResponse config = configuracionSistemaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(config, "Configuración actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Eliminar configuración")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        configuracionSistemaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Configuración eliminada exitosamente"));
    }

    @GetMapping("/smtp")
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Obtener configuración SMTP del sistema")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> obtenerConfiguracionSMTP() {
        java.util.Map<String, String> smtpConfig = configuracionSistemaService.obtenerConfiguracionPorCategoria("SMTP");
        return ResponseEntity.ok(ApiResponse.success(smtpConfig));
    }

    @PostMapping("/smtp")
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Guardar configuración SMTP del sistema")
    public ResponseEntity<ApiResponse<String>> guardarConfiguracionSMTP(
            @RequestBody java.util.Map<String, String> smtpConfig) {
        configuracionSistemaService.guardarConfiguracionSMTP(smtpConfig);
        return ResponseEntity.ok(ApiResponse.success(null, "Configuración SMTP guardada exitosamente"));
    }

    @PostMapping("/smtp/test")
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Probar configuración SMTP del sistema")
    public ResponseEntity<ApiResponse<String>> probarConfiguracionSMTP(@RequestParam String emailDestino) {
        boolean resultado = configuracionSistemaService.probarConfiguracionSMTP(emailDestino);
        if (resultado) {
            return ResponseEntity.ok(ApiResponse.success(null, "Correo de prueba enviado exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al enviar correo de prueba. Verifica la configuración SMTP."));
        }
    }

    @GetMapping("/clave/{clave}")
    @PreAuthorize("hasAuthority('CONFIG_VER')")
    @Operation(summary = "Obtener valor de configuración por clave")
    public ResponseEntity<ApiResponse<String>> obtenerPorClave(@PathVariable String clave) {
        String valor = configuracionSistemaService.obtenerValorPorClave(clave);
        return ResponseEntity.ok(ApiResponse.success(valor));
    }

    @PutMapping("/clave/{clave}")
    @PreAuthorize("hasAuthority('CONFIG_EDITAR')")
    @Operation(summary = "Actualizar valor de configuración por clave")
    public ResponseEntity<ApiResponse<String>> actualizarPorClave(
            @PathVariable String clave,
            @RequestBody java.util.Map<String, String> body) {
        String valor = body.get("valor");
        configuracionSistemaService.actualizarValorPorClave(clave, valor);
        return ResponseEntity.ok(ApiResponse.success(null, "Configuración actualizada exitosamente"));
    }
}


