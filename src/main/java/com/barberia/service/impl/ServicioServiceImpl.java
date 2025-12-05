package com.barberia.service.impl;

import com.barberia.dto.*;
import com.barberia.entity.Categoria;
import com.barberia.entity.Insumo;
import com.barberia.entity.Servicio;
import com.barberia.entity.Sucursal;
import com.barberia.exception.BusinessException;
import com.barberia.exception.ResourceNotFoundException;
import com.barberia.repository.CategoriaRepository;
import com.barberia.repository.InsumoRepository;
import com.barberia.repository.ServicioRepository;
import com.barberia.repository.SucursalRepository;
import com.barberia.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final SucursalRepository sucursalRepository;
    private final CategoriaRepository categoriaRepository;
    private final InsumoRepository insumoRepository;

    @Override
    @Transactional
    public ServicioResponse crear(ServicioRequest request) {
        // Validar código único
        if (servicioRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un servicio con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        Servicio servicio = new Servicio();
        servicio.setCodigo(request.getCodigo());
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());
        servicio.setTieneIva(request.getTieneIva());
        servicio.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            servicio.setCategoria(categoria);
        }

        if (request.getInsumosUtilizadosIds() != null && !request.getInsumosUtilizadosIds().isEmpty()) {
            Set<Insumo> insumos = new HashSet<>();
            for (Long insumoId : request.getInsumosUtilizadosIds()) {
                Insumo insumo = insumoRepository.findById(insumoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado: " + insumoId));
                insumos.add(insumo);
            }
            servicio.setInsumosUtilizados(insumos);
        }

        servicio = servicioRepository.save(servicio);
        return mapToResponse(servicio);
    }

    @Override
    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        // Validar código único si cambió
        if (!servicio.getCodigo().equals(request.getCodigo()) &&
                servicioRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un servicio con el código: " + request.getCodigo());
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        servicio.setCodigo(request.getCodigo());
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());
        servicio.setTieneIva(request.getTieneIva());
        servicio.setSucursal(sucursal);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            servicio.setCategoria(categoria);
        } else {
            servicio.setCategoria(null);
        }

        // Actualizar insumos
        servicio.getInsumosUtilizados().clear();
        if (request.getInsumosUtilizadosIds() != null && !request.getInsumosUtilizadosIds().isEmpty()) {
            Set<Insumo> insumos = new HashSet<>();
            for (Long insumoId : request.getInsumosUtilizadosIds()) {
                Insumo insumo = insumoRepository.findById(insumoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado: " + insumoId));
                insumos.add(insumo);
            }
            servicio.setInsumosUtilizados(insumos);
        }

        servicio = servicioRepository.save(servicio);
        return mapToResponse(servicio);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado");
        }
        servicioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse obtenerPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        return mapToResponse(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> listarTodos(Pageable pageable) {
        return servicioRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServicioResponse> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return servicioRepository.findBySucursalId(sucursalId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> listarPorSucursalSinPaginacion(Long sucursalId) {
        return servicioRepository.findBySucursalId(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse buscarPorCodigo(String codigo) {
        Servicio servicio = servicioRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con código: " + codigo));
        return mapToResponse(servicio);
    }

    private ServicioResponse mapToResponse(Servicio servicio) {
        ServicioResponse response = new ServicioResponse();
        response.setId(servicio.getId());
        response.setCodigo(servicio.getCodigo());
        response.setNombre(servicio.getNombre());
        response.setDescripcion(servicio.getDescripcion());
        response.setPrecio(servicio.getPrecio());
        response.setDuracionMinutos(servicio.getDuracionMinutos());
        response.setTieneIva(servicio.getTieneIva());
        response.setCreatedAt(servicio.getCreatedAt());
        response.setUpdatedAt(servicio.getUpdatedAt());

        if (servicio.getCategoria() != null) {
            CategoriaResponse categoriaResponse = new CategoriaResponse();
            categoriaResponse.setId(servicio.getCategoria().getId());
            categoriaResponse.setNombre(servicio.getCategoria().getNombre());
            categoriaResponse.setTipo(servicio.getCategoria().getTipo());
            response.setCategoria(categoriaResponse);
        }

        if (servicio.getSucursal() != null) {
            SucursalBasicResponse sucursalResponse = new SucursalBasicResponse();
            sucursalResponse.setId(servicio.getSucursal().getId());
            sucursalResponse.setNombre(servicio.getSucursal().getNombre());
            sucursalResponse.setDireccion(servicio.getSucursal().getDireccion());
            response.setSucursal(sucursalResponse);
        }

        if (servicio.getInsumosUtilizados() != null && !servicio.getInsumosUtilizados().isEmpty()) {
            Set<InsumoBasicResponse> insumosResponse = servicio.getInsumosUtilizados().stream()
                    .map(insumo -> {
                        InsumoBasicResponse insumoBasic = new InsumoBasicResponse();
                        insumoBasic.setId(insumo.getId());
                        insumoBasic.setCodigo(insumo.getCodigo());
                        insumoBasic.setNombre(insumo.getNombre());
                        insumoBasic.setUnidadMedida(insumo.getUnidadMedida());
                        return insumoBasic;
                    })
                    .collect(Collectors.toSet());
            response.setInsumosUtilizados(insumosResponse);
        }

        return response;
    }
}

