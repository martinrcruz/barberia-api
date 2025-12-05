package com.barberia.repository;

import com.barberia.entity.Documento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    @Query("SELECT d FROM Documento d WHERE d.sucursal.id = :sucursalId AND d.active = true")
    Page<Documento> findBySucursalId(@Param("sucursalId") Long sucursalId, Pageable pageable);

    @Query("SELECT d FROM Documento d WHERE d.categoriaDocumento = :categoria AND d.active = true")
    Page<Documento> findByCategoria(@Param("categoria") Documento.CategoriaDocumento categoria, Pageable pageable);

    @Query("SELECT d FROM Documento d WHERE d.sucursal.id = :sucursalId AND d.categoriaDocumento = :categoria AND d.active = true")
    Page<Documento> findBySucursalIdAndCategoria(@Param("sucursalId") Long sucursalId, 
                                                   @Param("categoria") Documento.CategoriaDocumento categoria, 
                                                   Pageable pageable);

    @Query("SELECT d FROM Documento d WHERE d.entidadRelacionadaTipo = :tipo AND d.entidadRelacionadaId = :id AND d.active = true")
    List<Documento> findByEntidadRelacionada(@Param("tipo") String tipo, @Param("id") Long id);

    @Query("SELECT d FROM Documento d WHERE LOWER(d.nombreOriginal) LIKE LOWER(CONCAT('%', :keyword, '%')) AND d.active = true")
    Page<Documento> searchByNombre(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT d FROM Documento d WHERE d.sucursal.id = :sucursalId AND LOWER(d.nombreOriginal) LIKE LOWER(CONCAT('%', :keyword, '%')) AND d.active = true")
    Page<Documento> searchBySucursalAndNombre(@Param("sucursalId") Long sucursalId, 
                                                @Param("keyword") String keyword, 
                                                Pageable pageable);
}
