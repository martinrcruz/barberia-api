package com.barberia.dto;

import com.barberia.entity.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaPadreId;
    private String categoriaPadreNombre;
    private Categoria.TipoCategoria tipo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

