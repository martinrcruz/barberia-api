package com.barberia.service.impl;

import com.barberia.dto.VentaRequest;
import com.barberia.dto.VentaResponse;
import com.barberia.entity.*;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.service.PdfService;

import java.io.ByteArrayOutputStream;
import com.barberia.repository.*;
import com.barberia.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;
    private final ClienteRepository clienteRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final RegistroContableRepository registroContableRepository;
    private final PdfService pdfService;

    @Value("${app.iva.rate:0.19}")
    private Double ivaRate;

    @Override
    @Transactional
    public VentaResponse crearVenta(VentaRequest request) {
        // Validar trabajador (usuario con rol TRABAJADOR)
        Usuario trabajador = usuarioRepository.findById(request.getTrabajadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getTrabajadorId()));

        boolean esTrabajador = trabajador.getRoles().stream()
                .anyMatch(rol -> "TRABAJADOR".equalsIgnoreCase(rol.getCodigo()));

        if (!esTrabajador) {
            throw new BusinessException("El usuario seleccionado no tiene el rol TRABAJADOR");
        }

        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.getSucursalId()));

        // Crear venta
        Venta venta = new Venta();
        venta.setNumeroVenta(generarNumeroVenta());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setTrabajador(trabajador);
        venta.setSucursal(sucursal);

        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));
            venta.setCliente(cliente);
        }
        venta.setMetodoPago(Venta.MetodoPago.valueOf(request.getMetodoPago()));
        venta.setObservaciones(request.getObservaciones());

        // Procesar detalles
        List<DetalleVenta> detalles = new ArrayList<>();
        double subtotal = 0.0;
        double ivaTotal = 0.0;

        for (VentaRequest.DetalleVentaRequest detalleReq : request.getDetalles()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setTipoItem(DetalleVenta.TipoItem.valueOf(detalleReq.getTipoItem()));
            detalle.setCantidad(detalleReq.getCantidad());
            detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());

            if ("PRODUCTO".equals(detalleReq.getTipoItem())) {
                if (detalleReq.getProductoId() == null) {
                    throw new BusinessException("El ID del producto es obligatorio para items de tipo PRODUCTO");
                }
                
                Producto producto = productoRepository.findById(detalleReq.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleReq.getProductoId()));
                
                // Verificar stock
                if (producto.getStockActual() < detalleReq.getCantidad()) {
                    throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre());
                }
                
                detalle.setProducto(producto);
                detalle.setDescripcion(producto.getNombre());
                detalle.setAplicaIva(producto.getTieneIva());

                // Actualizar stock
                producto.setStockActual(producto.getStockActual() - detalleReq.getCantidad());
                productoRepository.save(producto);

                // Registrar movimiento de inventario
                registrarMovimientoInventario(producto, null, sucursal, detalleReq.getCantidad(), 
                                             MovimientoInventario.TipoMovimiento.SALIDA, "Venta " + venta.getNumeroVenta());

            } else if ("SERVICIO".equals(detalleReq.getTipoItem())) {
                // Para servicios, permitir tanto servicios existentes como servicios ad-hoc
                if (detalleReq.getServicioId() != null) {
                    Servicio servicio = servicioRepository.findById(detalleReq.getServicioId())
                            .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", detalleReq.getServicioId()));
                    
                    detalle.setServicio(servicio);
                    detalle.setDescripcion(servicio.getNombre());
                    detalle.setAplicaIva(servicio.getTieneIva());
                } else {
                    // Servicio ad-hoc (sin ID, solo descripción y precio)
                    if (detalleReq.getDescripcion() == null || detalleReq.getDescripcion().isEmpty()) {
                        throw new BusinessException("La descripción del servicio es obligatoria");
                    }
                    detalle.setDescripcion(detalleReq.getDescripcion());
                    detalle.setAplicaIva(detalleReq.getAplicaIva() != null ? detalleReq.getAplicaIva() : true);
                }
            }

            double subtotalDetalle = detalleReq.getCantidad() * detalleReq.getPrecioUnitario();
            detalle.setSubtotal(subtotalDetalle);
            detalles.add(detalle);

            subtotal += subtotalDetalle;
            if (detalle.getAplicaIva()) {
                ivaTotal += subtotalDetalle * ivaRate;
            }
        }

        venta.setDetalles(detalles);
        venta.setSubtotal(subtotal);
        venta.setIva(ivaTotal);
        venta.setTotal(subtotal + ivaTotal);

        // Calcular comisión del trabajador
        double porcentajeComision = trabajador.getPorcentajeComision() != null ? trabajador.getPorcentajeComision() : 0.0;
        double comision = venta.getTotal() * (porcentajeComision / 100.0);
        venta.setComisionTrabajador(comision);

        venta = ventaRepository.save(venta);

        // Registrar en contabilidad
        registrarMovimientoContable(venta);

        return convertirAResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
        return convertirAResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> listarVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable).map(this::convertirAResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentasPorSucursal(Long sucursalId) {
        return ventaRepository.findBySucursalId(sucursalId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generarComprobante(Long ventaId) {
        log.info("Generando comprobante para venta: {}", ventaId);
        
        // Usar PdfService para generar el comprobante
        try {
            ByteArrayOutputStream outputStream = pdfService.generarComprobante(ventaId);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar comprobante para venta {}: {}", ventaId, e.getMessage());
            throw new BusinessException("Error al generar comprobante: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void anularVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
        
        // Revertir stock
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getTipoItem() == DetalleVenta.TipoItem.PRODUCTO && detalle.getProducto() != null) {
                Producto producto = detalle.getProducto();
                producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        venta.setActive(false);
        ventaRepository.save(venta);
    }

    private String generarNumeroVenta() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "V-" + timestamp;
    }

    private void registrarMovimientoInventario(Producto producto, Insumo insumo, Sucursal sucursal,
                                              Integer cantidad, MovimientoInventario.TipoMovimiento tipo, String motivo) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setSucursal(sucursal);
        movimiento.setMotivo(motivo);

        if (producto != null) {
            movimiento.setTipoItem(MovimientoInventario.TipoItemInventario.PRODUCTO);
            movimiento.setProducto(producto);
            movimiento.setStockAnterior(producto.getStockActual() + cantidad);
            movimiento.setStockNuevo(producto.getStockActual());
        } else if (insumo != null) {
            movimiento.setTipoItem(MovimientoInventario.TipoItemInventario.INSUMO);
            movimiento.setInsumo(insumo);
            movimiento.setStockAnterior(insumo.getStockActual() + cantidad);
            movimiento.setStockNuevo(insumo.getStockActual());
        }

        movimientoInventarioRepository.save(movimiento);
    }

    private void registrarMovimientoContable(Venta venta) {
        // Ingreso por venta
        RegistroContable registro = new RegistroContable();
        registro.setFechaRegistro(LocalDateTime.now());
        registro.setTipoRegistro(RegistroContable.TipoRegistro.INGRESO);
        registro.setCategoria(RegistroContable.CategoriaContable.VENTA);
        registro.setMonto(venta.getTotal());
        registro.setDescripcion("Venta " + venta.getNumeroVenta());
        registro.setSucursal(venta.getSucursal());
        registro.setVenta(venta);
        registroContableRepository.save(registro);

        // Egreso por comisión
        if (venta.getComisionTrabajador() != null && venta.getComisionTrabajador() > 0) {
            RegistroContable comision = new RegistroContable();
            comision.setFechaRegistro(LocalDateTime.now());
            comision.setTipoRegistro(RegistroContable.TipoRegistro.EGRESO);
            comision.setCategoria(RegistroContable.CategoriaContable.COMISION);
            comision.setMonto(venta.getComisionTrabajador());
            comision.setDescripcion("Comisión venta " + venta.getNumeroVenta() + " - " + venta.getTrabajador().getNombreCompleto());
            comision.setSucursal(venta.getSucursal());
            comision.setVenta(venta);
            registroContableRepository.save(comision);
        }
    }

    private VentaResponse convertirAResponse(Venta venta) {
        List<VentaResponse.DetalleVentaResponse> detallesResponse = venta.getDetalles().stream()
                .map(detalle -> VentaResponse.DetalleVentaResponse.builder()
                        .id(detalle.getId())
                        .tipoItem(detalle.getTipoItem().name())
                        .descripcion(detalle.getDescripcion())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .aplicaIva(detalle.getAplicaIva())
                        .build())
                .collect(Collectors.toList());

        String trabajadorNombre = null;
        try {
            trabajadorNombre = venta.getTrabajador() != null ? venta.getTrabajador().getNombreCompleto() : null;
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            // Si el trabajador ya no existe en BD, evitamos que falle el listado de ventas
            trabajadorNombre = "[Trabajador eliminado]";
        }

        return VentaResponse.builder()
                .id(venta.getId())
                .numeroVenta(venta.getNumeroVenta())
                .fechaVenta(venta.getFechaVenta())
                .trabajadorNombre(trabajadorNombre)
                .clienteNombre(venta.getCliente() != null ? venta.getCliente().getNombreCompleto() : null)
                .sucursalNombre(venta.getSucursal().getNombre())
                .subtotal(venta.getSubtotal())
                .iva(venta.getIva())
                .total(venta.getTotal())
                .comisionTrabajador(venta.getComisionTrabajador())
                .metodoPago(venta.getMetodoPago().name())
                .observaciones(venta.getObservaciones())
                .detalles(detallesResponse)
                .comprobanteUrl(venta.getComprobanteUrl())
                .build();
    }
}

