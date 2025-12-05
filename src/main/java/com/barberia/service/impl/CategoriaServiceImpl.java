package com.barberia.service.impl;

import com.barberia.dto.CategoriaRequest;
import com.barberia.dto.CategoriaResponse;
import com.barberia.entity.Categoria;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.CategoriaRepository;
import com.barberia.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        // Validar nombre único por tipo
        if (categoriaRepository.existsByNombreAndTipo(request.getNombre(), request.getTipo())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre() + " para el tipo " + request.getTipo());
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setTipo(request.getTipo());

        if (request.getCategoriaPadreId() != null) {
            Categoria categoriaPadre = categoriaRepository.findById(request.getCategoriaPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada"));
            categoria.setCategoriaPadre(categoriaPadre);
        }

        categoria = categoriaRepository.save(categoria);
        return mapToResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Validar nombre único si cambió
        if (!categoria.getNombre().equals(request.getNombre()) &&
                categoriaRepository.existsByNombreAndTipo(request.getNombre(), request.getTipo())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre() + " para el tipo " + request.getTipo());
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setTipo(request.getTipo());

        if (request.getCategoriaPadreId() != null) {
            if (request.getCategoriaPadreId().equals(id)) {
                throw new BusinessException("Una categoría no puede ser su propia categoría padre");
            }
            Categoria categoriaPadre = categoriaRepository.findById(request.getCategoriaPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada"));
            categoria.setCategoriaPadre(categoriaPadre);
        } else {
            categoria.setCategoriaPadre(null);
        }

        categoria = categoriaRepository.save(categoria);
        return mapToResponse(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return mapToResponse(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarTodos(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodasSinPaginacion() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarPorTipo(Categoria.TipoCategoria tipo) {
        return categoriaRepository.findByTipo(tipo).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoriaResponse mapToResponse(Categoria categoria) {
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setTipo(categoria.getTipo());
        response.setCreatedAt(categoria.getCreatedAt());
        response.setUpdatedAt(categoria.getUpdatedAt());

        if (categoria.getCategoriaPadre() != null) {
            response.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            response.setCategoriaPadreNombre(categoria.getCategoriaPadre().getNombre());
        }

        return response;
    }
}

