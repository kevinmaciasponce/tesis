package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaEstadoFinanciero;

public interface PersonaEstadoFinancieroRepository extends JpaRepository<PersonaEstadoFinanciero, Long>{

	PersonaEstadoFinanciero findByIdEstFinan(Long id);
}
