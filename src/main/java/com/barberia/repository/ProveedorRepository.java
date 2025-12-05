package com.barberia.repository;

import com.barberia.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    Optional<Proveedor> findByRut(String rut);
    
    boolean existsByRut(String rut);
    
    Page<Proveedor> findByActiveTrue(Pageable pageable);
    
    List<Proveedor> findByActiveTrue();
}

