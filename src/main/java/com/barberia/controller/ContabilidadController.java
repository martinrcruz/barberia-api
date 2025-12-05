package com.barberia.controller;

import com.barberia.dto.ApiResponse;
import com.barberia.dto.RegistroContableResponse;
import com.barberia.dto.ResumenContableResponse;
import com.barberia.entity.Sucursal;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.ContabilidadService;
import com.barberia.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/contabilidad")
@RequiredArgsConstructor
@Tag(name = "Contabilidad", description = "Consultas contables y reportes financieros")
@SecurityRequirement(name = "bearerAuth")
public class ContabilidadController {

    private final ContabilidadService contabilidadService;
    private final ExportService exportService;
    private final SucursalRepository sucursalRepository;

    @GetMapping("/registros")
    @PreAuthorize("hasAuthority('CONTABILIDAD_VER')")
    @Operation(summary = "Listar registros contables")
    public ResponseEntity<ApiResponse<List<RegistroContableResponse>>> listarRegistros(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        List<RegistroContableResponse> registros = contabilidadService.listarRegistros(sucursalId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(registros));
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('CONTABILIDAD_VER')")
    @Operation(summary = "Obtener resumen contable")
    public ResponseEntity<ApiResponse<ResumenContableResponse>> obtenerResumen(
            @RequestParam Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        ResumenContableResponse resumen = contabilidadService.obtenerResumen(sucursalId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.success(resumen));
    }

    @GetMapping("/exportar/excel")
    @PreAuthorize("hasAuthority('CONTABILIDAD_VER')")
    @Operation(summary = "Exportar registros contables a Excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        try {
            List<RegistroContableResponse> registros = contabilidadService.listarRegistros(sucursalId, fechaInicio, fechaFin);
            
            String nombreSucursal = "TODAS_LAS_SUCURSALES";
            if (sucursalId != null) {
                Sucursal sucursal = sucursalRepository.findById(sucursalId).orElse(null);
                if (sucursal != null) {
                    nombreSucursal = sucursal.getNombre();
                }
            }

            ByteArrayOutputStream outputStream = exportService.exportarContabilidadExcel(
                registros, 
                fechaInicio, 
                fechaFin, 
                nombreSucursal
            );

            String nombreArchivo = "Contabilidad_" + nombreSucursal + "_" + 
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", nombreArchivo);

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/exportar/pdf")
    @PreAuthorize("hasAuthority('CONTABILIDAD_VER')")
    @Operation(summary = "Exportar registros contables a PDF")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        try {
            List<RegistroContableResponse> registros = contabilidadService.listarRegistros(sucursalId, fechaInicio, fechaFin);
            
            String nombreSucursal = "TODAS LAS SUCURSALES";
            if (sucursalId != null) {
                Sucursal sucursal = sucursalRepository.findById(sucursalId).orElse(null);
                if (sucursal != null) {
                    nombreSucursal = sucursal.getNombre();
                }
            }

            ByteArrayOutputStream outputStream = exportService.exportarContabilidadPDF(
                registros, 
                fechaInicio, 
                fechaFin, 
                nombreSucursal
            );

            String nombreArchivo = "Contabilidad_" + nombreSucursal.replace(" ", "_") + "_" + 
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nombreArchivo);

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAuthority('CONTABILIDAD_VER')")
    @Operation(summary = "Obtener estadísticas para dashboard financiero")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> obtenerEstadisticasDashboard(
            @RequestParam Long sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        ResumenContableResponse resumen = contabilidadService.obtenerResumen(sucursalId, fechaInicio, fechaFin);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalIngresos", resumen.getTotalIngresos());
        stats.put("totalEgresos", resumen.getTotalEgresos());
        stats.put("gananciaNeta", resumen.getGananciaNeta());
        stats.put("cantidadRegistros", resumen.getCantidadRegistros());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}


