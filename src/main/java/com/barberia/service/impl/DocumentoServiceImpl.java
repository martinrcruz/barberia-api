package com.barberia.service.impl;

import com.barberia.dto.DocumentoRequest;
import com.barberia.dto.DocumentoResponse;
import com.barberia.entity.Documento;
import com.barberia.entity.Sucursal;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.DocumentoRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.DocumentoService;
import com.barberia.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final SucursalRepository sucursalRepository;
    private final FileStorageService fileStorageService;

    @Override
    public DocumentoResponse subirDocumento(MultipartFile archivo, DocumentoRequest request) throws IOException {
        log.info("Subiendo documento: {}", archivo.getOriginalFilename());

        // Validar categoría
        String categoria = request.getCategoriaDocumento().name();

        // Almacenar archivo (con o sin compresión)
        String rutaRelativa;
        if (Boolean.TRUE.equals(request.getComprimirImagen()) && fileStorageService.esImagen(archivo)) {
            int calidad = request.getCalidadCompresion() != null ? request.getCalidadCompresion() : 85;
            rutaRelativa = fileStorageService.almacenarArchivoConCompresion(archivo, categoria, request.getSucursalId(), calidad);
        } else {
            rutaRelativa = fileStorageService.almacenarArchivo(archivo, categoria, request.getSucursalId());
        }

        // Crear entidad Documento
        Documento documento = new Documento();
        documento.setNombreOriginal(archivo.getOriginalFilename());
        documento.setNombreArchivo(fileStorageService.generarNombreArchivoUnico(archivo.getOriginalFilename()));
        documento.setRutaArchivo(rutaRelativa);
        documento.setTipoMime(fileStorageService.obtenerTipoMime(archivo));
        documento.setTamano(archivo.getSize());
        documento.setCategoriaDocumento(request.getCategoriaDocumento());
        documento.setEntidadRelacionadaTipo(request.getEntidadRelacionadaTipo());
        documento.setEntidadRelacionadaId(request.getEntidadRelacionadaId());

        if (request.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
            documento.setSucursal(sucursal);
        }

        Documento guardado = documentoRepository.save(documento);
        log.info("Documento subido exitosamente con ID: {}", guardado.getId());

        return mapearRespuesta(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoResponse obtenerPorId(Long id) {
        log.info("Obteniendo documento con ID: {}", id);

        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));

        return mapearRespuesta(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> obtenerTodos(Pageable pageable) {
        log.info("Obteniendo todos los documentos");

        return documentoRepository.findAll(pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> obtenerPorSucursal(Long sucursalId, Pageable pageable) {
        log.info("Obteniendo documentos por sucursal ID: {}", sucursalId);

        return documentoRepository.findBySucursalId(sucursalId, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> obtenerPorCategoria(Documento.CategoriaDocumento categoria, Pageable pageable) {
        log.info("Obteniendo documentos por categoría: {}", categoria);

        return documentoRepository.findByCategoria(categoria, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> obtenerPorSucursalYCategoria(Long sucursalId, Documento.CategoriaDocumento categoria, Pageable pageable) {
        log.info("Obteniendo documentos por sucursal ID: {} y categoría: {}", sucursalId, categoria);

        return documentoRepository.findBySucursalIdAndCategoria(sucursalId, categoria, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoResponse> obtenerPorEntidadRelacionada(String tipo, Long id) {
        log.info("Obteniendo documentos por entidad relacionada: {} ID: {}", tipo, id);

        return documentoRepository.findByEntidadRelacionada(tipo, id).stream()
            .map(this::mapearRespuesta)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> buscarPorNombre(String keyword, Pageable pageable) {
        log.info("Buscando documentos por nombre: {}", keyword);

        return documentoRepository.searchByNombre(keyword, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoResponse> buscarPorSucursalYNombre(Long sucursalId, String keyword, Pageable pageable) {
        log.info("Buscando documentos por sucursal ID: {} y nombre: {}", sucursalId, keyword);

        return documentoRepository.searchBySucursalAndNombre(sucursalId, keyword, pageable)
            .map(this::mapearRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource descargarDocumento(Long id) throws IOException {
        log.info("Descargando documento con ID: {}", id);

        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));

        Path rutaArchivo = fileStorageService.obtenerRutaCompleta(documento.getRutaArchivo());

        Resource resource = new UrlResource(rutaArchivo.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ResourceNotFoundException("Archivo no encontrado o no se puede leer: " + documento.getNombreOriginal());
        }
    }

    @Override
    public void eliminarDocumento(Long id) throws IOException {
        log.info("Eliminando documento con ID: {}", id);

        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));

        // Eliminar archivo físico
        fileStorageService.eliminarArchivo(documento.getRutaArchivo());

        // Desactivar registro en BD
        documento.setActive(false);
        documentoRepository.save(documento);

        log.info("Documento eliminado exitosamente");
    }

    // Métodos auxiliares

    private DocumentoResponse mapearRespuesta(Documento documento) {
        DocumentoResponse response = new DocumentoResponse();
        response.setId(documento.getId());
        response.setNombreOriginal(documento.getNombreOriginal());
        response.setNombreArchivo(documento.getNombreArchivo());
        response.setRutaArchivo(documento.getRutaArchivo());
        response.setUrlDescarga("/api/documentos/" + documento.getId() + "/descargar");
        response.setTipoMime(documento.getTipoMime());
        response.setTamano(documento.getTamano());
        response.setTamanoLegible(formatearTamano(documento.getTamano()));
        response.setCategoriaDocumento(documento.getCategoriaDocumento());
        
        if (documento.getSucursal() != null) {
            response.setSucursalId(documento.getSucursal().getId());
            response.setNombreSucursal(documento.getSucursal().getNombre());
        }
        
        response.setEntidadRelacionadaTipo(documento.getEntidadRelacionadaTipo());
        response.setEntidadRelacionadaId(documento.getEntidadRelacionadaId());
        response.setActive(documento.getActive());
        response.setCreatedAt(documento.getCreatedAt());
        response.setUpdatedAt(documento.getUpdatedAt());
        response.setCreatedBy(documento.getCreatedBy());
        response.setUpdatedBy(documento.getUpdatedBy());

        return response;
    }

    private String formatearTamano(Long tamanoBytes) {
        if (tamanoBytes == null || tamanoBytes == 0) {
            return "0 B";
        }

        String[] unidades = {"B", "KB", "MB", "GB"};
        int unidadIndex = 0;
        double tamano = tamanoBytes;

        while (tamano >= 1024 && unidadIndex < unidades.length - 1) {
            tamano /= 1024;
            unidadIndex++;
        }

        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(tamano) + " " + unidades[unidadIndex];
    }
}

