package com.barberia.repository;

import com.barberia.entity.RegistroContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroContableRepository extends JpaRepository<RegistroContable, Long> {
    
    List<RegistroContable> findBySucursalId(Long sucursalId);
    
    List<RegistroContable> findByTipoRegistro(RegistroContable.TipoRegistro tipoRegistro);
    
    List<RegistroContable> findByFechaRegistroBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT SUM(r.monto) FROM RegistroContable r WHERE r.tipoRegistro = :tipo AND r.sucursal.id = :sucursalId AND r.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    Double sumMontoByTipoAndSucursalAndFecha(
            @Param("tipo") RegistroContable.TipoRegistro tipo,
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}

