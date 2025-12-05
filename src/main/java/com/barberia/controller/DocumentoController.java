package com.barberia.controller;

import com.barberia.dto.DocumentoRequest;
import com.barberia.dto.DocumentoResponse;
import com.barberia.entity.Documento;
import com.barberia.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "API para gestión de documentos y archivos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Subir documento")
    public ResponseEntity<Map<String, Object>> subirDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("categoriaDocumento") Documento.CategoriaDocumento categoriaDocumento,
            @RequestParam(value = "sucursalId", required = false) Long sucursalId,
            @RequestParam(value = "entidadRelacionadaTipo", required = false) String entidadRelacionadaTipo,
            @RequestParam(value = "entidadRelacionadaId", required = false) Long entidadRelacionadaId,
            @RequestParam(value = "comprimirImagen", required = false, defaultValue = "false") Boolean comprimirImagen,
            @RequestParam(value = "calidadCompresion", required = false, defaultValue = "85") Integer calidadCompresion) {
        try {
            DocumentoRequest request = new DocumentoRequest();
            request.setCategoriaDocumento(categoriaDocumento);
            request.setSucursalId(sucursalId);
            request.setEntidadRelacionadaTipo(entidadRelacionadaTipo);
            request.setEntidadRelacionadaId(entidadRelacionadaId);
            request.setComprimirImagen(comprimirImagen);
            request.setCalidadCompresion(calidadCompresion);

            DocumentoResponse response = documentoService.subirDocumento(archivo, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Documento subido exitosamente");
            result.put("data", response);

            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al subir el documento: " + e.getMessage());

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener documento por ID")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        DocumentoResponse response = documentoService.obtenerPorId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener todos los documentos con paginación")
    public ResponseEntity<Map<String, Object>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DocumentoResponse> response = documentoService.obtenerTodos(pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener documentos por sucursal")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursal(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentoResponse> response = documentoService.obtenerPorSucursal(sucursalId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener documentos por categoría")
    public ResponseEntity<Map<String, Object>> obtenerPorCategoria(
            @PathVariable Documento.CategoriaDocumento categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentoResponse> response = documentoService.obtenerPorCategoria(categoria, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/categoria/{categoria}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener documentos por sucursal y categoría")
    public ResponseEntity<Map<String, Object>> obtenerPorSucursalYCategoria(
            @PathVariable Long sucursalId,
            @PathVariable Documento.CategoriaDocumento categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentoResponse> response = documentoService.obtenerPorSucursalYCategoria(sucursalId, categoria, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/entidad/{tipo}/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener documentos por entidad relacionada")
    public ResponseEntity<Map<String, Object>> obtenerPorEntidadRelacionada(
            @PathVariable String tipo,
            @PathVariable Long id) {

        List<DocumentoResponse> response = documentoService.obtenerPorEntidadRelacionada(tipo, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar documentos por nombre")
    public ResponseEntity<Map<String, Object>> buscarPorNombre(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentoResponse> response = documentoService.buscarPorNombre(keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/sucursal/{sucursalId}/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar documentos por sucursal y nombre")
    public ResponseEntity<Map<String, Object>> buscarPorSucursalYNombre(
            @PathVariable Long sucursalId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentoResponse> response = documentoService.buscarPorSucursalYNombre(sucursalId, keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/descargar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Descargar documento")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        try {
            Resource resource = documentoService.descargarDocumento(id);
            DocumentoResponse documento = documentoService.obtenerPorId(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(documento.getTipoMime()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNombreOriginal() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DOCUMENTO_ELIMINAR')")
    @Operation(summary = "Eliminar documento")
    public ResponseEntity<Map<String, Object>> eliminarDocumento(@PathVariable Long id) {
        try {
            documentoService.eliminarDocumento(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Documento eliminado exitosamente");

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al eliminar el documento: " + e.getMessage());

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

