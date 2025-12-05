package com.barberia.repository;

import com.barberia.entity.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {
    
    List<VarianteProducto> findByProductoIdAndActiveTrueOrderByOrdenAsc(Long productoId);
    
    Optional<VarianteProducto> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    List<VarianteProducto> findByActiveTrueOrderByOrdenAsc();
}

