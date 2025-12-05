package com.barberia.repository;

import com.barberia.entity.ConfiguracionSucursal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionSucursalRepository extends JpaRepository<ConfiguracionSucursal, Long> {

    /**
     * Busca una configuración específica por sucursal y clave
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND c.clave = :clave AND c.active = true")
    Optional<ConfiguracionSucursal> findBySucursalIdAndClave(@Param("sucursalId") Long sucursalId, @Param("clave") String clave);

    /**
     * Obtiene todas las configuraciones de una sucursal
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND c.active = true ORDER BY c.categoria, c.clave")
    List<ConfiguracionSucursal> findAllBySucursalId(@Param("sucursalId") Long sucursalId);

    /**
     * Obtiene configuraciones de una sucursal con paginación
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND c.active = true")
    Page<ConfiguracionSucursal> findBySucursalId(@Param("sucursalId") Long sucursalId, Pageable pageable);

    /**
     * Obtiene configuraciones por categoría
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND c.categoria = :categoria AND c.active = true")
    List<ConfiguracionSucursal> findBySucursalIdAndCategoria(@Param("sucursalId") Long sucursalId, @Param("categoria") String categoria);

    /**
     * Busca configuraciones por palabra clave en clave o descripción
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND " +
           "(LOWER(c.clave) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND c.active = true")
    Page<ConfiguracionSucursal> searchByKeyword(@Param("sucursalId") Long sucursalId, @Param("keyword") String keyword, Pageable pageable);

    /**
     * Verifica si existe una configuración específica
     */
    boolean existsBySucursalIdAndClaveAndActiveTrue(Long sucursalId, String clave);

    /**
     * Obtiene configuraciones obligatorias de una sucursal
     */
    @Query("SELECT c FROM ConfiguracionSucursal c WHERE c.sucursal.id = :sucursalId AND c.esObligatoria = true AND c.active = true")
    List<ConfiguracionSucursal> findConfiguracionesObligatorias(@Param("sucursalId") Long sucursalId);
}

