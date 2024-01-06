package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaRepresentanteLegal;

public interface PersonaRepresentanteLegalRepository extends JpaRepository<PersonaRepresentanteLegal, Long>{

	PersonaRepresentanteLegal findByIdRepreLegal(Long id);
}
