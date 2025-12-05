package com.barberia.repository;

import com.barberia.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByRut(String rut);
    
    Optional<Usuario> findByTokenRecuperacion(String token);
    
    @Query("SELECT u FROM Usuario u JOIN FETCH u.roles r JOIN FETCH r.permisos WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRolesAndPermissions(String email);
}

