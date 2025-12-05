package com.barberia.service.impl;

import com.barberia.dto.VarianteProductoRequest;
import com.barberia.dto.VarianteProductoResponse;
import com.barberia.entity.Producto;
import com.barberia.entity.VarianteProducto;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.ProductoRepository;
import com.barberia.repository.VarianteProductoRepository;
import com.barberia.service.VarianteProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VarianteProductoServiceImpl implements VarianteProductoService {

    private final VarianteProductoRepository varianteProductoRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public VarianteProductoResponse crear(VarianteProductoRequest request) {
        log.info("Creando nueva variante de producto: {}", request.getNombre());

        // Validar producto
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        // Validar SKU único si se proporciona
        if (request.getSku() != null && !request.getSku().isEmpty()) {
            if (varianteProductoRepository.existsBySku(request.getSku())) {
                throw new BusinessException("Ya existe una variante con el SKU: " + request.getSku());
            }
        }

        VarianteProducto variante = new VarianteProducto();
        variante.setProducto(producto);
        variante.setNombre(request.getNombre());
        variante.setSku(request.getSku());
        variante.setPrecioVenta(request.getPrecioVenta());
        variante.setPrecioCosto(request.getPrecioCosto());
        variante.setStockActual(request.getStockActual() != null ? request.getStockActual() : 0);
        variante.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 0);
        variante.setAtributos(request.getAtributos());
        variante.setImagenUrl(request.getImagenUrl());
        variante.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        variante.setActive(true);

        variante = varianteProductoRepository.save(variante);

        // Marcar producto como con variantes
        if (!Boolean.TRUE.equals(producto.getTieneVariantes())) {
            producto.setTieneVariantes(true);
            productoRepository.save(producto);
        }

        log.info("Variante de producto creada exitosamente con ID: {}", variante.getId());
        return mapToResponse(variante);
    }

    @Override
    @Transactional
    public VarianteProductoResponse actualizar(Long id, VarianteProductoRequest request) {
        log.info("Actualizando variante de producto con ID: {}", id);

        VarianteProducto variante = varianteProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada con ID: " + id));

        // Validar SKU único si cambió
        if (request.getSku() != null && !request.getSku().isEmpty()) {
            varianteProductoRepository.findBySku(request.getSku()).ifPresent(v -> {
                if (!v.getId().equals(id)) {
                    throw new BusinessException("Ya existe otra variante con el SKU: " + request.getSku());
                }
            });
        }

        variante.setNombre(request.getNombre());
        variante.setSku(request.getSku());
        variante.setPrecioVenta(request.getPrecioVenta());
        variante.setPrecioCosto(request.getPrecioCosto());
        variante.setStockActual(request.getStockActual() != null ? request.getStockActual() : variante.getStockActual());
        variante.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : variante.getStockMinimo());
        variante.setAtributos(request.getAtributos());
        variante.setImagenUrl(request.getImagenUrl());
        variante.setOrden(request.getOrden() != null ? request.getOrden() : variante.getOrden());

        variante = varianteProductoRepository.save(variante);
        log.info("Variante de producto actualizada exitosamente con ID: {}", id);

        return mapToResponse(variante);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando variante de producto con ID: {}", id);

        VarianteProducto variante = varianteProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada con ID: " + id));

        // Borrado lógico
        variante.setActive(false);
        varianteProductoRepository.save(variante);
        log.info("Variante de producto eliminada exitosamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public VarianteProductoResponse obtenerPorId(Long id) {
        log.info("Obteniendo variante de producto con ID: {}", id);

        VarianteProducto variante = varianteProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada con ID: " + id));

        return mapToResponse(variante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VarianteProductoResponse> listarPorProducto(Long productoId) {
        log.info("Listando variantes del producto ID: {}", productoId);

        // Verificar que el producto existe
        productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        return varianteProductoRepository.findByProductoIdAndActiveTrueOrderByOrdenAsc(productoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VarianteProductoResponse obtenerPorSku(String sku) {
        log.info("Obteniendo variante de producto con SKU: {}", sku);

        VarianteProducto variante = varianteProductoRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de producto no encontrada con SKU: " + sku));

        return mapToResponse(variante);
    }

    private VarianteProductoResponse mapToResponse(VarianteProducto variante) {
        VarianteProductoResponse response = new VarianteProductoResponse();
        response.setId(variante.getId());
        response.setProductoId(variante.getProducto().getId());
        response.setProductoNombre(variante.getProducto().getNombre());
        response.setNombre(variante.getNombre());
        response.setSku(variante.getSku());
        response.setPrecioVenta(variante.getPrecioVenta());
        response.setPrecioCosto(variante.getPrecioCosto());
        response.setPrecioVentaEfectivo(variante.getPrecioVentaEfectivo());
        response.setPrecioCostoEfectivo(variante.getPrecioCostoEfectivo());
        response.setStockActual(variante.getStockActual());
        response.setStockMinimo(variante.getStockMinimo());
        response.setAtributos(variante.getAtributos());
        response.setImagenUrl(variante.getImagenUrl());
        response.setOrden(variante.getOrden());
        response.setActivo(variante.getActive() != null && variante.getActive());
        response.setCreatedAt(variante.getCreatedAt());
        response.setUpdatedAt(variante.getUpdatedAt());
        return response;
    }
}

