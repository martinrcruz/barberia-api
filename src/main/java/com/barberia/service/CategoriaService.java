package com.barberia.service;

import com.barberia.dto.CategoriaRequest;
import com.barberia.dto.CategoriaResponse;
import com.barberia.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void eliminar(Long id);

    CategoriaResponse obtenerPorId(Long id);

    Page<CategoriaResponse> listarTodos(Pageable pageable);

    List<CategoriaResponse> listarTodasSinPaginacion();

    List<CategoriaResponse> listarPorTipo(Categoria.TipoCategoria tipo);
}

