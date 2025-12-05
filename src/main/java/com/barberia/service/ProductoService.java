package com.barberia.service;

import com.barberia.dto.ProductoRequest;
import com.barberia.dto.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);

    ProductoResponse obtenerPorId(Long id);

    Page<ProductoResponse> listarTodos(Pageable pageable);

    Page<ProductoResponse> listarPorSucursal(Long sucursalId, Pageable pageable);

    List<ProductoResponse> listarPorSucursalSinPaginacion(Long sucursalId);

    List<ProductoResponse> listarConStockBajo(Long sucursalId);

    ProductoResponse actualizarStock(Long id, Integer cantidad, String tipo);

    ProductoResponse buscarPorCodigo(String codigo);
}

