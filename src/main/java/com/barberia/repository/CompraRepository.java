package com.barberia.repository;

import com.barberia.entity.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    Optional<Compra> findByNumeroCompra(String numeroCompra);
    
    List<Compra> findBySucursalId(Long sucursalId);
    
    Page<Compra> findBySucursalIdOrderByFechaCompraDesc(Long sucursalId, Pageable pageable);
    
    List<Compra> findByProveedorId(Long proveedorId);
    
    Page<Compra> findByProveedorIdOrderByFechaCompraDesc(Long proveedorId, Pageable pageable);
    
    List<Compra> findByFechaCompraBetweenOrderByFechaCompraDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Compra> findBySucursalIdAndFechaCompraBetweenOrderByFechaCompraDesc(Long sucursalId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT SUM(c.total) FROM Compra c WHERE c.sucursal.id = :sucursalId AND c.fechaCompra BETWEEN :fechaInicio AND :fechaFin")
    Double sumTotalBySucursalAndFecha(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}

