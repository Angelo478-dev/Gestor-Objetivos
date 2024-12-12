package com.objetivos.objetivo_service.repository;

import com.objetivos.objetivo_service.model.entity.Objetivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IObjetivoRepository extends JpaRepository<Objetivo, Long> {

    @Query("SELECT t FROM Objetivo t where upper(t.titulo) = upper(:titulo) ")
    List<Objetivo> findByTitulo(String titulo);
    @Query(value = "SELECT * FROM objetivo WHERE usuario_id = :usuarioId", nativeQuery = true)
    List<Objetivo> findByUsuarioId(Long usuarioId);
}

