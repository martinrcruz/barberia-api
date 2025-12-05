package com.barberia.repository;

import com.barberia.entity.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    
    Optional<Insumo> findByCodigo(String codigo);
    
    List<Insumo> findBySucursalId(Long sucursalId);
    
    Page<Insumo> findBySucursalId(Long sucursalId, Pageable pageable);
    
    List<Insumo> findByCategoriaId(Long categoriaId);
    
    @Query("SELECT i FROM Insumo i WHERE i.stockActual <= i.stockMinimo")
    List<Insumo> findInsumosConStockBajo();
    
    @Query("SELECT i FROM Insumo i WHERE i.sucursal.id = :sucursalId AND i.stockActual <= i.stockMinimo")
    List<Insumo> findBySucursalIdAndStockActualLessThanEqualStockMinimo(Long sucursalId);
    
    boolean existsByCodigo(String codigo);
}

