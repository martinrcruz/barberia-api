package com.barberia.service.impl;

import com.barberia.dto.*;
import com.barberia.entity.*;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.*;
import com.barberia.service.CompraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest request) {
        log.info("Registrando nueva compra para proveedor ID: {} en sucursal ID: {}", 
                 request.getProveedorId(), request.getSucursalId());

        // Validar proveedor
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + request.getProveedorId()));

        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        // Crear compra
        Compra compra = new Compra();
        compra.setNumeroCompra(generarNumeroCompra());
        compra.setFechaCompra(request.getFechaCompra() != null ? request.getFechaCompra() : LocalDateTime.now());
        compra.setProveedor(proveedor);
        compra.setSucursal(sucursal);
        compra.setNumeroDocumento(request.getNumeroDocumento());
        compra.setTipoDocumento(request.getTipoDocumento());
        compra.setObservaciones(request.getObservaciones());
        compra.setActive(true);

        // Procesar detalles
        List<DetalleCompra> detalles = new ArrayList<>();
        double subtotalTotal = 0.0;

        for (CompraRequest.DetalleCompraRequest detalleReq : request.getDetalles()) {
            DetalleCompra detalle = procesarDetalleCompra(detalleReq, compra, sucursal);
            detalles.add(detalle);
            subtotalTotal += detalle.getSubtotal();
        }

        compra.setDetalles(detalles);
        compra.setSubtotal(subtotalTotal);
        
        // Calcular IVA (19% en Chile)
        double iva = subtotalTotal * 0.19;
        compra.setIva(iva);
        compra.setTotal(subtotalTotal + iva);

        compra = compraRepository.save(compra);
        log.info("Compra registrada exitosamente con ID: {}. Total: ${}", compra.getId(), compra.getTotal());

        return mapToResponse(compra);
    }

    private DetalleCompra procesarDetalleCompra(CompraRequest.DetalleCompraRequest detalleReq, 
                                                 Compra compra, 
                                                 Sucursal sucursal) {
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCompra(compra);
        detalle.setCantidad(detalleReq.getCantidad());
        detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
        detalle.setSubtotal(detalleReq.getCantidad() * detalleReq.getPrecioUnitario());
        detalle.setDescripcion(detalleReq.getDescripcion());

        DetalleCompra.TipoItemCompra tipoItem;
        try {
            tipoItem = DetalleCompra.TipoItemCompra.valueOf(detalleReq.getTipoItem().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de item inválido: " + detalleReq.getTipoItem());
        }
        detalle.setTipoItem(tipoItem);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFechaMovimiento(compra.getFechaCompra());
        movimiento.setTipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA);
        movimiento.setSucursal(sucursal);
        movimiento.setCantidad(detalleReq.getCantidad());
        movimiento.setMotivo("Compra #" + compra.getNumeroCompra());
        movimiento.setReferenciaDocumento(compra.getNumeroDocumento());
        movimiento.setActive(true);

        if (tipoItem == DetalleCompra.TipoItemCompra.PRODUCTO) {
            // Validar producto
            if (detalleReq.getProductoId() == null) {
                throw new BusinessException("Debe especificar el ID del producto para items de tipo PRODUCTO");
            }
            
            Producto producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReq.getProductoId()));

            detalle.setProducto(producto);
            
            // Actualizar stock del producto
            int stockAnterior = producto.getStockActual() != null ? producto.getStockActual() : 0;
            int nuevoStock = stockAnterior + detalleReq.getCantidad();
            producto.setStockActual(nuevoStock);
            producto.setPrecioCosto(detalleReq.getPrecioUnitario()); // Actualizar precio de costo
            productoRepository.save(producto);
            
            // Registrar movimiento en Kardex
            movimiento.setProducto(producto);
            movimiento.setInsumo(null);
            movimiento.setTipoItem(MovimientoInventario.TipoItemInventario.PRODUCTO);
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(nuevoStock);
            
            log.info("Stock de producto {} actualizado: {} -> {}", producto.getCodigo(), stockAnterior, nuevoStock);

        } else if (tipoItem == DetalleCompra.TipoItemCompra.INSUMO) {
            // Validar insumo
            if (detalleReq.getInsumoId() == null) {
                throw new BusinessException("Debe especificar el ID del insumo para items de tipo INSUMO");
            }
            
            Insumo insumo = insumoRepository.findById(detalleReq.getInsumoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con ID: " + detalleReq.getInsumoId()));

            detalle.setInsumo(insumo);
            
            // Actualizar stock del insumo
            int stockAnterior = insumo.getStockActual() != null ? insumo.getStockActual() : 0;
            int nuevoStock = stockAnterior + detalleReq.getCantidad();
            insumo.setStockActual(nuevoStock);
            insumo.setPrecioUnitario(detalleReq.getPrecioUnitario()); // Actualizar precio unitario
            insumoRepository.save(insumo);
            
            // Registrar movimiento en Kardex
            movimiento.setProducto(null);
            movimiento.setInsumo(insumo);
            movimiento.setTipoItem(MovimientoInventario.TipoItemInventario.INSUMO);
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(nuevoStock);
            
            log.info("Stock de insumo {} actualizado: {} -> {}", insumo.getCodigo(), stockAnterior, nuevoStock);
        }

        movimientoInventarioRepository.save(movimiento);
        return detalle;
    }

    private String generarNumeroCompra() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "COM-" + timestamp;
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponse obtenerPorId(Long id) {
        log.info("Obteniendo compra con ID: {}", id);
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con ID: " + id));
        return mapToResponse(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> listarTodos(Pageable pageable) {
        log.info("Listando todas las compras con paginación");
        return compraRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        log.info("Listando compras de la sucursal ID: {}", sucursalId);
        return compraRepository.findBySucursalIdOrderByFechaCompraDesc(sucursalId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> listarPorProveedor(Long proveedorId, Pageable pageable) {
        log.info("Listando compras del proveedor ID: {}", proveedorId);
        return compraRepository.findByProveedorIdOrderByFechaCompraDesc(proveedorId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> listarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando compras entre {} y {}", fechaInicio, fechaFin);
        return compraRepository.findByFechaCompraBetweenOrderByFechaCompraDesc(fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> listarPorSucursalYFechas(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Listando compras de la sucursal ID: {} entre {} y {}", sucursalId, fechaInicio, fechaFin);
        return compraRepository.findBySucursalIdAndFechaCompraBetweenOrderByFechaCompraDesc(sucursalId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CompraResponse mapToResponse(Compra compra) {
        CompraResponse response = new CompraResponse();
        response.setId(compra.getId());
        response.setNumeroCompra(compra.getNumeroCompra());
        response.setFechaCompra(compra.getFechaCompra());
        response.setSubtotal(compra.getSubtotal());
        response.setIva(compra.getIva());
        response.setTotal(compra.getTotal());
        response.setNumeroDocumento(compra.getNumeroDocumento());
        response.setTipoDocumento(compra.getTipoDocumento());
        response.setObservaciones(compra.getObservaciones());
        response.setCreatedAt(compra.getCreatedAt());
        response.setUpdatedAt(compra.getUpdatedAt());

        // Mapear proveedor (usando ProveedorResponse simplificado)
        ProveedorResponse proveedorResponse = new ProveedorResponse();
        proveedorResponse.setId(compra.getProveedor().getId());
        proveedorResponse.setRut(compra.getProveedor().getRut());
        proveedorResponse.setRazonSocial(compra.getProveedor().getRazonSocial());
        proveedorResponse.setNombreFantasia(compra.getProveedor().getNombreFantasia());
        response.setProveedor(proveedorResponse);

        // Mapear sucursal
        SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
        sucursalResponse.setId(compra.getSucursal().getId());
        sucursalResponse.setNombre(compra.getSucursal().getNombre());
        sucursalResponse.setDireccion(compra.getSucursal().getDireccion());
        response.setSucursal(sucursalResponse);

        // Mapear detalles
        if (compra.getDetalles() != null && !compra.getDetalles().isEmpty()) {
            List<CompraResponse.DetalleCompraResponse> detallesResponse = compra.getDetalles().stream()
                    .map(this::mapDetalleToResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detallesResponse);
        }

        return response;
    }

    private CompraResponse.DetalleCompraResponse mapDetalleToResponse(DetalleCompra detalle) {
        CompraResponse.DetalleCompraResponse response = new CompraResponse.DetalleCompraResponse();
        response.setId(detalle.getId());
        response.setTipoItem(detalle.getTipoItem().name());
        response.setDescripcion(detalle.getDescripcion());
        response.setCantidad(detalle.getCantidad());
        response.setPrecioUnitario(detalle.getPrecioUnitario());
        response.setSubtotal(detalle.getSubtotal());

        // Mapear producto si aplica
        if (detalle.getProducto() != null) {
            ProductoResponse productoResponse = new ProductoResponse();
            productoResponse.setId(detalle.getProducto().getId());
            productoResponse.setCodigo(detalle.getProducto().getCodigo());
            productoResponse.setNombre(detalle.getProducto().getNombre());
            productoResponse.setPrecioVenta(detalle.getProducto().getPrecioVenta());
            productoResponse.setPrecioCosto(detalle.getProducto().getPrecioCosto());
            response.setProducto(productoResponse);
        }

        // Mapear insumo si aplica
        if (detalle.getInsumo() != null) {
            InsumoResponse insumoResponse = new InsumoResponse();
            insumoResponse.setId(detalle.getInsumo().getId());
            insumoResponse.setCodigo(detalle.getInsumo().getCodigo());
            insumoResponse.setNombre(detalle.getInsumo().getNombre());
            insumoResponse.setPrecioUnitario(detalle.getInsumo().getPrecioUnitario());
            response.setInsumo(insumoResponse);
        }

        return response;
    }
}

