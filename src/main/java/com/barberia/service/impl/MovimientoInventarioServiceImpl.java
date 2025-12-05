package com.barberia.service.impl;

import com.barberia.dto.*;
import com.barberia.entity.*;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.*;
import com.barberia.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;
    private final InsumoRepository insumoRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    @Transactional
    public MovimientoInventarioResponse registrarAjuste(MovimientoInventarioRequest request) {
        log.info("Registrando ajuste de inventario: {}", request.getMotivo());

        // Validar que sea un movimiento de tipo AJUSTE
        if (request.getTipoMovimiento() != MovimientoInventario.TipoMovimiento.AJUSTE) {
            throw new BusinessException("Este método solo permite registrar ajustes de inventario. " +
                    "Los movimientos de entrada/salida se registran automáticamente en compras/ventas.");
        }

        // Validar motivo obligatorio para ajustes
        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new BusinessException("El motivo es obligatorio para ajustes de inventario");
        }

        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFechaMovimiento(request.getFechaMovimiento() != null ? request.getFechaMovimiento() : LocalDateTime.now());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setTipoItem(request.getTipoItem());
        movimiento.setSucursal(sucursal);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setReferenciaDocumento(request.getReferenciaDocumento());
        movimiento.setActive(true);

        // Procesar según tipo de item
        if (request.getTipoItem() == MovimientoInventario.TipoItemInventario.PRODUCTO) {
            if (request.getProductoId() == null) {
                throw new BusinessException("Debe especificar el ID del producto para ajustes de productos");
            }

            Producto producto = productoRepository.findById(request.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

            movimiento.setProducto(producto);
            movimiento.setInsumo(null);

            int stockAnterior = producto.getStockActual() != null ? producto.getStockActual() : 0;
            int nuevoStock = stockAnterior + request.getCantidad(); // Cantidad puede ser negativa para salidas

            if (nuevoStock < 0) {
                throw new BusinessException("El ajuste resultaría en un stock negativo. Stock actual: " + stockAnterior);
            }

            producto.setStockActual(nuevoStock);
            productoRepository.save(producto);

            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(nuevoStock);

            log.info("Stock de producto {} ajustado: {} -> {}. Motivo: {}", 
                     producto.getCodigo(), stockAnterior, nuevoStock, request.getMotivo());

        } else if (request.getTipoItem() == MovimientoInventario.TipoItemInventario.INSUMO) {
            if (request.getInsumoId() == null) {
                throw new BusinessException("Debe especificar el ID del insumo para ajustes de insumos");
            }

            Insumo insumo = insumoRepository.findById(request.getInsumoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con ID: " + request.getInsumoId()));

            movimiento.setProducto(null);
            movimiento.setInsumo(insumo);

            int stockAnterior = insumo.getStockActual() != null ? insumo.getStockActual() : 0;
            int nuevoStock = stockAnterior + request.getCantidad(); // Cantidad puede ser negativa para salidas

            if (nuevoStock < 0) {
                throw new BusinessException("El ajuste resultaría en un stock negativo. Stock actual: " + stockAnterior);
            }

            insumo.setStockActual(nuevoStock);
            insumoRepository.save(insumo);

            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(nuevoStock);

            log.info("Stock de insumo {} ajustado: {} -> {}. Motivo: {}", 
                     insumo.getCodigo(), stockAnterior, nuevoStock, request.getMotivo());
        }

        movimiento = movimientoInventarioRepository.save(movimiento);
        log.info("Ajuste de inventario registrado exitosamente con ID: {}", movimiento.getId());

        return mapToResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoInventarioResponse obtenerPorId(Long id) {
        log.info("Obteniendo movimiento de inventario con ID: {}", id);
        MovimientoInventario movimiento = movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento de inventario no encontrado con ID: " + id));
        return mapToResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponse> listarTodos(Pageable pageable) {
        log.info("Listando todos los movimientos de inventario con paginación");
        return movimientoInventarioRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        log.info("Listando movimientos de inventario de la sucursal ID: {}", sucursalId);
        return movimientoInventarioRepository.findBySucursalIdOrderByFechaMovimientoDesc(sucursalId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarKardexPorProducto(Long productoId) {
        log.info("Listando Kardex del producto ID: {}", productoId);
        
        // Verificar que el producto existe
        productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        return movimientoInventarioRepository.findByProductoIdOrderByFechaMovimientoAsc(productoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarKardexPorInsumo(Long insumoId) {
        log.info("Listando Kardex del insumo ID: {}", insumoId);
        
        // Verificar que el insumo existe
        insumoRepository.findById(insumoId)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con ID: " + insumoId));

        return movimientoInventarioRepository.findByInsumoIdOrderByFechaMovimientoAsc(insumoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando movimientos de inventario entre {} y {}", fechaInicio, fechaFin);
        return movimientoInventarioRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponse> listarPorTipo(MovimientoInventario.TipoMovimiento tipoMovimiento, Pageable pageable) {
        log.info("Listando movimientos de inventario por tipo: {}", tipoMovimiento);
        return movimientoInventarioRepository.findByTipoMovimientoOrderByFechaMovimientoDesc(tipoMovimiento, pageable)
                .map(this::mapToResponse);
    }

    private MovimientoInventarioResponse mapToResponse(MovimientoInventario movimiento) {
        MovimientoInventarioResponse response = new MovimientoInventarioResponse();
        response.setId(movimiento.getId());
        response.setFechaMovimiento(movimiento.getFechaMovimiento());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setTipoItem(movimiento.getTipoItem());
        response.setCantidad(movimiento.getCantidad());
        response.setStockAnterior(movimiento.getStockAnterior());
        response.setStockNuevo(movimiento.getStockNuevo());
        response.setMotivo(movimiento.getMotivo());
        response.setReferenciaDocumento(movimiento.getReferenciaDocumento());
        response.setCreatedAt(movimiento.getCreatedAt());
        response.setUpdatedAt(movimiento.getUpdatedAt());

        // Mapear producto si aplica
        if (movimiento.getProducto() != null) {
            ProductoResponse productoResponse = new ProductoResponse();
            productoResponse.setId(movimiento.getProducto().getId());
            productoResponse.setCodigo(movimiento.getProducto().getCodigo());
            productoResponse.setNombre(movimiento.getProducto().getNombre());
            productoResponse.setStockActual(movimiento.getProducto().getStockActual());
            response.setProducto(productoResponse);
        }

        // Mapear insumo si aplica
        if (movimiento.getInsumo() != null) {
            InsumoResponse insumoResponse = new InsumoResponse();
            insumoResponse.setId(movimiento.getInsumo().getId());
            insumoResponse.setCodigo(movimiento.getInsumo().getCodigo());
            insumoResponse.setNombre(movimiento.getInsumo().getNombre());
            insumoResponse.setStockActual(movimiento.getInsumo().getStockActual());
            response.setInsumo(insumoResponse);
        }

        // Mapear sucursal
        SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
        sucursalResponse.setId(movimiento.getSucursal().getId());
        sucursalResponse.setNombre(movimiento.getSucursal().getNombre());
        sucursalResponse.setDireccion(movimiento.getSucursal().getDireccion());
        response.setSucursal(sucursalResponse);

        return response;
    }
}

