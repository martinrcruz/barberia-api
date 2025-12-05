package com.barberia.repository;

import com.barberia.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCodigo(String codigo);
    
    List<Producto> findBySucursalId(Long sucursalId);
    
    Page<Producto> findBySucursalId(Long sucursalId, Pageable pageable);
    
    List<Producto> findByCategoriaId(Long categoriaId);
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();
    
    @Query("SELECT p FROM Producto p WHERE p.sucursal.id = :sucursalId AND p.stockActual <= p.stockMinimo")
    List<Producto> findBySucursalIdAndStockActualLessThanEqualStockMinimo(Long sucursalId);
    
    boolean existsByCodigo(String codigo);
}

