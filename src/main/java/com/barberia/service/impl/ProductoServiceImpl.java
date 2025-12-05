package com.barberia.service.impl;

import com.barberia.dto.CategoriaResponse;
import com.barberia.dto.ProductoRequest;
import com.barberia.dto.ProductoResponse;
import com.barberia.dto.SucursalBasicResponse;
import com.barberia.entity.Categoria;
import com.barberia.entity.Producto;
import com.barberia.entity.Sucursal;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.CategoriaRepository;
import com.barberia.repository.ProductoRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        // Validar código único
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un producto con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setPrecioCosto(request.getPrecioCosto());
        producto.setStockActual(request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setTieneIva(request.getTieneIva());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        producto = productoRepository.save(producto);
        return mapToResponse(producto);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Validar código único si cambió
        if (!producto.getCodigo().equals(request.getCodigo()) &&
                productoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un producto con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setPrecioCosto(request.getPrecioCosto());
        producto.setStockActual(request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setTieneIva(request.getTieneIva());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }

        producto = productoRepository.save(producto);
        return mapToResponse(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return mapToResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarTodos(Pageable pageable) {
        return productoRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return productoRepository.findBySucursalId(sucursalId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorSucursalSinPaginacion(Long sucursalId) {
        return productoRepository.findBySucursalId(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarConStockBajo(Long sucursalId) {
        return productoRepository.findBySucursalIdAndStockActualLessThanEqualStockMinimo(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponse actualizarStock(Long id, Integer cantidad, String tipo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Integer stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;

        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            producto.setStockActual(stockActual + cantidad);
        } else if ("SALIDA".equalsIgnoreCase(tipo)) {
            if (stockActual < cantidad) {
                throw new BusinessException("Stock insuficiente. Stock actual: " + stockActual);
            }
            producto.setStockActual(stockActual - cantidad);
        } else {
            throw new BusinessException("Tipo de movimiento no válido. Use 'ENTRADA' o 'SALIDA'");
        }

        producto = productoRepository.save(producto);
        return mapToResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + codigo));
        return mapToResponse(producto);
    }

    private ProductoResponse mapToResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setCodigo(producto.getCodigo());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecioVenta(producto.getPrecioVenta());
        response.setPrecioCosto(producto.getPrecioCosto());
        response.setStockActual(producto.getStockActual());
        response.setStockMinimo(producto.getStockMinimo());
        response.setTieneIva(producto.getTieneIva());
        response.setImagenUrl(producto.getImagenUrl());
        response.setUnidadMedida(producto.getUnidadMedida());
        response.setCreatedAt(producto.getCreatedAt());
        response.setUpdatedAt(producto.getUpdatedAt());

        if (producto.getCategoria() != null) {
            CategoriaResponse categoriaResponse = new CategoriaResponse();
            categoriaResponse.setId(producto.getCategoria().getId());
            categoriaResponse.setNombre(producto.getCategoria().getNombre());
            categoriaResponse.setTipo(producto.getCategoria().getTipo());
            response.setCategoria(categoriaResponse);
        }

        if (producto.getSucursal() != null) {
            SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
            sucursalResponse.setId(producto.getSucursal().getId());
            sucursalResponse.setNombre(producto.getSucursal().getNombre());
            sucursalResponse.setDireccion(producto.getSucursal().getDireccion());
            response.setSucursal(sucursalResponse);
        }

        return response;
    }
}

