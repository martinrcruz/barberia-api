package com.barberia.repository;

import com.barberia.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    Optional<Servicio> findByCodigo(String codigo);
    
    List<Servicio> findBySucursalId(Long sucursalId);
    
    Page<Servicio> findBySucursalId(Long sucursalId, Pageable pageable);
    
    List<Servicio> findByCategoriaId(Long categoriaId);
    
    List<Servicio> findByActiveTrue();
    
    boolean existsByCodigo(String codigo);
}

