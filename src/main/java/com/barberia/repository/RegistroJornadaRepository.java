package com.barberia.repository;

import com.barberia.entity.RegistroJornada;
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
public interface RegistroJornadaRepository extends JpaRepository<RegistroJornada, Long> {
    
    // Buscar jornada activa de un usuario en una sucursal
    Optional<RegistroJornada> findByUsuarioIdAndSucursalIdAndEstado(
            Long usuarioId, 
            Long sucursalId, 
            RegistroJornada.EstadoJornada estado
    );
    
    // Buscar jornadas por usuario
    Page<RegistroJornada> findByUsuarioIdOrderByFechaEntradaDesc(Long usuarioId, Pageable pageable);
    
    // Buscar jornadas por sucursal
    Page<RegistroJornada> findBySucursalIdOrderByFechaEntradaDesc(Long sucursalId, Pageable pageable);
    
    // Buscar jornadas por rango de fechas
    @Query("SELECT rj FROM RegistroJornada rj WHERE rj.fechaEntrada >= :fechaInicio AND rj.fechaEntrada <= :fechaFin ORDER BY rj.fechaEntrada DESC")
    Page<RegistroJornada> findByFechaEntradaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );
    
    // Buscar jornadas por usuario y rango de fechas
    @Query("SELECT rj FROM RegistroJornada rj WHERE rj.usuario.id = :usuarioId AND rj.fechaEntrada >= :fechaInicio AND rj.fechaEntrada <= :fechaFin ORDER BY rj.fechaEntrada DESC")
    List<RegistroJornada> findByUsuarioIdAndFechaEntradaBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Buscar jornadas por sucursal y rango de fechas
    @Query("SELECT rj FROM RegistroJornada rj WHERE rj.sucursal.id = :sucursalId AND rj.fechaEntrada >= :fechaInicio AND rj.fechaEntrada <= :fechaFin ORDER BY rj.fechaEntrada DESC")
    List<RegistroJornada> findBySucursalIdAndFechaEntradaBetween(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}

