package com.barberia.repository;

import com.barberia.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByCodigo(String codigo);
    
    Optional<Rol> findByNombre(String nombre);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByNombre(String nombre);
}

