package com.barberia.repository;

import com.barberia.entity.PersonalizacionVisual;
import com.barberia.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalizacionVisualRepository extends JpaRepository<PersonalizacionVisual, Long> {

    /**
     * Busca la personalización global (sin sucursal específica)
     */
    @Query("SELECT p FROM PersonalizacionVisual p WHERE p.esGlobal = true AND p.active = true")
    Optional<PersonalizacionVisual> findPersonalizacionGlobal();

    /**
     * Busca la personalización de una sucursal específica
     */
    @Query("SELECT p FROM PersonalizacionVisual p WHERE p.sucursal = :sucursal AND p.active = true")
    Optional<PersonalizacionVisual> findBySucursal(@Param("sucursal") Sucursal sucursal);

    /**
     * Busca personalización por ID de sucursal
     */
    @Query("SELECT p FROM PersonalizacionVisual p WHERE p.sucursal.id = :sucursalId AND p.active = true")
    Optional<PersonalizacionVisual> findBySucursalId(@Param("sucursalId") Long sucursalId);

    /**
     * Verifica si existe una personalización global
     */
    boolean existsByEsGlobalTrueAndActiveTrue();

    /**
     * Verifica si existe una personalización para una sucursal
     */
    boolean existsBySucursalIdAndActiveTrue(Long sucursalId);
}

