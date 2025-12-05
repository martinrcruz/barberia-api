package com.barberia.repository;

import com.barberia.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    List<Categoria> findByTipo(Categoria.TipoCategoria tipo);
    
    List<Categoria> findByCategoriaPadreIsNull();
    
    List<Categoria> findByCategoriaPadreId(Long categoriaPadreId);
    
    boolean existsByNombreAndTipo(String nombre, Categoria.TipoCategoria tipo);
}

