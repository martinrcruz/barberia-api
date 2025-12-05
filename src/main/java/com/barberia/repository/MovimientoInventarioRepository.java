package com.barberia.repository;

import com.barberia.entity.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    
    List<MovimientoInventario> findByProductoId(Long productoId);
    
    List<MovimientoInventario> findByInsumoId(Long insumoId);
    
    List<MovimientoInventario> findBySucursalId(Long sucursalId);
    
    Page<MovimientoInventario> findBySucursalIdOrderByFechaMovimientoDesc(Long sucursalId, Pageable pageable);
    
    List<MovimientoInventario> findByProductoIdOrderByFechaMovimientoAsc(Long productoId);
    
    List<MovimientoInventario> findByInsumoIdOrderByFechaMovimientoAsc(Long insumoId);
    
    List<MovimientoInventario> findByFechaMovimientoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MovimientoInventario> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Page<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(MovimientoInventario.TipoMovimiento tipoMovimiento, Pageable pageable);
}

