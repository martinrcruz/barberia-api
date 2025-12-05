package com.barberia.repository;

import com.barberia.entity.Permiso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    
    Optional<Permiso> findByCodigo(String codigo);
    
    Optional<Permiso> findByNombre(String nombre);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByNombre(String nombre);
    
    Page<Permiso> findByActiveTrue(Pageable pageable);
    
    List<Permiso> findByActiveTrue();
    
    List<Permiso> findByTipoAndActiveTrue(Permiso.TipoPermiso tipo);
}

