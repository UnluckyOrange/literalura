package com.unlucky_orange.Desafio.repository;

import com.unlucky_orange.Desafio.entity.LibroEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<LibroEntity, Long> {

    @EntityGraph(attributePaths = "autores")
    List<LibroEntity> findAll(); // Fuerza cargar autores

    List<LibroEntity> findByIdiomaIgnoreCase(String idioma);

    List<LibroEntity> findByIlustradoTrue();

    List<LibroEntity> findByTituloContainingIgnoreCase(String titulo);
}
