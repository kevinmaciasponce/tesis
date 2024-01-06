package com.multiplos.cuentas.auspicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Disciplina;

public interface CategoriaRepository extends JpaRepository<Categorias, Long> {

}
