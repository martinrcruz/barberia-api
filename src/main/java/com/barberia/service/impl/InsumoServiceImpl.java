package com.barberia.service.impl;

import com.barberia.dto.CategoriaResponse;
import com.barberia.dto.InsumoRequest;
import com.barberia.dto.InsumoResponse;
import com.barberia.dto.SucursalBasicResponse;
import com.barberia.entity.Categoria;
import com.barberia.entity.Insumo;
import com.barberia.entity.Sucursal;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.CategoriaRepository;
import com.barberia.repository.InsumoRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.InsumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsumoServiceImpl implements InsumoService {

    private final InsumoRepository insumoRepository;
    private final SucursalRepository sucursalRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public InsumoResponse crear(InsumoRequest request) {
        // Validar código único
        if (insumoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un insumo con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        Insumo insumo = new Insumo();
        insumo.setCodigo(request.getCodigo());
        insumo.setNombre(request.getNombre());
        insumo.setDescripcion(request.getDescripcion());
        insumo.setStockActual(request.getStockActual());
        insumo.setStockMinimo(request.getStockMinimo());
        insumo.setUnidadMedida(request.getUnidadMedida());
        insumo.setPrecioUnitario(request.getPrecioUnitario());
        insumo.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            insumo.setCategoria(categoria);
        }

        insumo = insumoRepository.save(insumo);
        return mapToResponse(insumo);
    }

    @Override
    @Transactional
    public InsumoResponse actualizar(Long id, InsumoRequest request) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));

        // Validar código único si cambió
        if (!insumo.getCodigo().equals(request.getCodigo()) &&
                insumoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un insumo con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        insumo.setCodigo(request.getCodigo());
        insumo.setNombre(request.getNombre());
        insumo.setDescripcion(request.getDescripcion());
        insumo.setStockActual(request.getStockActual());
        insumo.setStockMinimo(request.getStockMinimo());
        insumo.setUnidadMedida(request.getUnidadMedida());
        insumo.setPrecioUnitario(request.getPrecioUnitario());
        insumo.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            insumo.setCategoria(categoria);
        } else {
            insumo.setCategoria(null);
        }

        insumo = insumoRepository.save(insumo);
        return mapToResponse(insumo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!insumoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Insumo no encontrado");
        }
        insumoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InsumoResponse obtenerPorId(Long id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));
        return mapToResponse(insumo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsumoResponse> listarTodos(Pageable pageable) {
        return insumoRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsumoResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return insumoRepository.findBySucursalId(sucursalId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponse> listarPorSucursalSinPaginacion(Long sucursalId) {
        return insumoRepository.findBySucursalId(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponse> listarConStockBajo(Long sucursalId) {
        return insumoRepository.findBySucursalIdAndStockActualLessThanEqualStockMinimo(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InsumoResponse actualizarStock(Long id, Integer cantidad, String tipo) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado"));

        Integer stockActual = insumo.getStockActual() != null ? insumo.getStockActual() : 0;

        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            insumo.setStockActual(stockActual + cantidad);
        } else if ("SALIDA".equalsIgnoreCase(tipo)) {
            if (stockActual < cantidad) {
                throw new BusinessException("Stock insuficiente. Stock actual: " + stockActual);
            }
            insumo.setStockActual(stockActual - cantidad);
        } else {
            throw new BusinessException("Tipo de movimiento no válido. Use 'ENTRADA' o 'SALIDA'");
        }

        insumo = insumoRepository.save(insumo);
        return mapToResponse(insumo);
    }

    private InsumoResponse mapToResponse(Insumo insumo) {
        InsumoResponse response = new InsumoResponse();
        response.setId(insumo.getId());
        response.setCodigo(insumo.getCodigo());
        response.setNombre(insumo.getNombre());
        response.setDescripcion(insumo.getDescripcion());
        response.setStockActual(insumo.getStockActual());
        response.setStockMinimo(insumo.getStockMinimo());
        response.setUnidadMedida(insumo.getUnidadMedida());
        response.setPrecioUnitario(insumo.getPrecioUnitario());
        response.setCreatedAt(insumo.getCreatedAt());
        response.setUpdatedAt(insumo.getUpdatedAt());

        if (insumo.getCategoria() != null) {
            CategoriaResponse categoriaResponse = new CategoriaResponse();
            categoriaResponse.setId(insumo.getCategoria().getId());
            categoriaResponse.setNombre(insumo.getCategoria().getNombre());
            categoriaResponse.setTipo(insumo.getCategoria().getTipo());
            response.setCategoria(categoriaResponse);
        }

        if (insumo.getSucursal() != null) {
            SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
            sucursalResponse.setId(insumo.getSucursal().getId());
            sucursalResponse.setNombre(insumo.getSucursal().getNombre());
            sucursalResponse.setDireccion(insumo.getSucursal().getDireccion());
            response.setSucursal(sucursalResponse);
        }

        return response;
    }
}

