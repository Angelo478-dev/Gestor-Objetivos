package com.objetivos.usuario_service.repository;

import com.objetivos.usuario_service.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("SELECT t FROM Usuario t where upper(t.nombre) = upper(:nombre) ")
    List<Usuario> findByName(String nombre);
    @Query("SELECT t FROM Usuario t WHERE upper(t.email) = upper(:email)")  // Corrected to use 'email'
    List<Usuario> findByEmail(String email);
}

