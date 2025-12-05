package com.barberia.repository;

import com.barberia.entity.MetodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    
    Optional<MetodoPago> findByCodigo(String codigo);
    
    Optional<MetodoPago> findByNombre(String nombre);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByNombre(String nombre);
    
    Page<MetodoPago> findByActiveTrue(Pageable pageable);
    
    List<MetodoPago> findByActiveTrueOrderByOrdenAsc();
}

