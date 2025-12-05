package com.barberia.service;

import com.barberia.dto.DocumentoRequest;
import com.barberia.dto.DocumentoResponse;
import com.barberia.entity.Documento;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentoService {

    DocumentoResponse subirDocumento(MultipartFile archivo, DocumentoRequest request) throws IOException;

    DocumentoResponse obtenerPorId(Long id);

    Page<DocumentoResponse> obtenerTodos(Pageable pageable);

    Page<DocumentoResponse> obtenerPorSucursal(Long sucursalId, Pageable pageable);

    Page<DocumentoResponse> obtenerPorCategoria(Documento.CategoriaDocumento categoria, Pageable pageable);

    Page<DocumentoResponse> obtenerPorSucursalYCategoria(Long sucursalId, Documento.CategoriaDocumento categoria, Pageable pageable);

    List<DocumentoResponse> obtenerPorEntidadRelacionada(String tipo, Long id);

    Page<DocumentoResponse> buscarPorNombre(String keyword, Pageable pageable);

    Page<DocumentoResponse> buscarPorSucursalYNombre(Long sucursalId, String keyword, Pageable pageable);

    Resource descargarDocumento(Long id) throws IOException;

    void eliminarDocumento(Long id) throws IOException;
}

