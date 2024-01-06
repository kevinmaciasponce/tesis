package com.multiplos.cuentas.auspicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.auspicios.models.Disciplina;
import com.multiplos.cuentas.auspicios.models.Modalidad;

public interface ModalidadRepository extends JpaRepository<Modalidad, Long> {

}
