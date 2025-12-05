package com.barberia.repository;

import com.barberia.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    
    List<Venta> findBySucursalId(Long sucursalId);
    
    List<Venta> findByTrabajadorId(Long trabajadorId);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaVentaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT v FROM Venta v WHERE v.sucursal.id = :sucursalId AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findBySucursalIdAndFechaVentaBetween(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.sucursal.id = :sucursalId AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Double sumTotalBySucursalAndFecha(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}

