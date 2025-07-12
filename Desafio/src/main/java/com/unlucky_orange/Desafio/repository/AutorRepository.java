package com.unlucky_orange.Desafio.repository;

import com.unlucky_orange.Desafio.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<AutorEntity, Long> {
    List<AutorEntity> findByFechaDeMuerteIsNull(); // autores vivos
    List<AutorEntity> findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(String nacimiento, String muerte);
}
