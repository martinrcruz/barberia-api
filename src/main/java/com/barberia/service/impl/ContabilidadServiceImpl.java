package com.barberia.service.impl;

import com.barberia.dto.RegistroContableResponse;
import com.barberia.dto.ResumenContableResponse;
import com.barberia.entity.RegistroContable;
import com.barberia.repository.RegistroContableRepository;
import com.barberia.service.ContabilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContabilidadServiceImpl implements ContabilidadService {

    private final RegistroContableRepository registroContableRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RegistroContableResponse> listarRegistros(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<RegistroContable> registros;

        if (sucursalId != null && fechaInicio != null && fechaFin != null) {
            registros = registroContableRepository.findBySucursalId(sucursalId).stream()
                    .filter(r -> !r.getFechaRegistro().isBefore(fechaInicio) && !r.getFechaRegistro().isAfter(fechaFin))
                    .collect(Collectors.toList());
        } else if (sucursalId != null) {
            registros = registroContableRepository.findBySucursalId(sucursalId);
        } else if (fechaInicio != null && fechaFin != null) {
            registros = registroContableRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
        } else {
            registros = registroContableRepository.findAll();
        }

        return registros.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenContableResponse obtenerResumen(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        LocalDateTime inicio = fechaInicio != null ? fechaInicio : LocalDateTime.now().minusDays(30);
        LocalDateTime fin = fechaFin != null ? fechaFin : LocalDateTime.now();

        Double ingresos = registroContableRepository.sumMontoByTipoAndSucursalAndFecha(
                RegistroContable.TipoRegistro.INGRESO,
                sucursalId,
                inicio,
                fin
        );

        Double egresos = registroContableRepository.sumMontoByTipoAndSucursalAndFecha(
                RegistroContable.TipoRegistro.EGRESO,
                sucursalId,
                inicio,
                fin
        );

        double totalIngresos = ingresos != null ? ingresos : 0.0;
        double totalEgresos = egresos != null ? egresos : 0.0;
        double gananciaNeta = totalIngresos - totalEgresos;

        long cantidadRegistros = registroContableRepository.count();

        return ResumenContableResponse.builder()
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .balance(gananciaNeta)
                .gananciaNeta(gananciaNeta)
                .cantidadRegistros(cantidadRegistros)
                .build();
    }

    private RegistroContableResponse mapToResponse(RegistroContable registro) {
        return RegistroContableResponse.builder()
                .id(registro.getId())
                .fechaRegistro(registro.getFechaRegistro())
                .tipoMovimiento(registro.getTipoRegistro().name())
                .categoria(registro.getCategoria().name())
                .monto(registro.getMonto() != null ? java.math.BigDecimal.valueOf(registro.getMonto()) : null)
                .concepto(registro.getDescripcion())
                .sucursalId(registro.getSucursal() != null ? registro.getSucursal().getId() : null)
                .nombreSucursal(registro.getSucursal() != null ? registro.getSucursal().getNombre() : null)
                .ventaId(registro.getVenta() != null ? registro.getVenta().getId() : null)
                .compraId(registro.getCompra() != null ? registro.getCompra().getId() : null)
                .referenciaMovimiento(registro.getReferencia())
                .build();
    }
}


