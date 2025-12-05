package com.barberia.service;

import com.barberia.dto.VarianteProductoRequest;
import com.barberia.dto.VarianteProductoResponse;

import java.util.List;

public interface VarianteProductoService {
    VarianteProductoResponse crear(VarianteProductoRequest request);
    VarianteProductoResponse actualizar(Long id, VarianteProductoRequest request);
    void eliminar(Long id);
    VarianteProductoResponse obtenerPorId(Long id);
    List<VarianteProductoResponse> listarPorProducto(Long productoId);
    VarianteProductoResponse obtenerPorSku(String sku);
}

