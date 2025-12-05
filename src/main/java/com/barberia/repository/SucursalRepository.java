package com.barberia.repository;

import com.barberia.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    
    List<Sucursal> findByActiveTrue();
    
    boolean existsByNombre(String nombre);
}

