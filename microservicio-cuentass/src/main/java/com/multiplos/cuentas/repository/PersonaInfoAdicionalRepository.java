package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.PersonaInfoAdicional;

public interface PersonaInfoAdicionalRepository extends JpaRepository<PersonaInfoAdicional, Long> {

	@Query("select i from PersonaInfoAdicional i where i.persona.identificacion=?1 and i.estado='A'")
	PersonaInfoAdicional consultaInfoAdicional(String identificacion);
	
	@Query("select i.idInfoAdicional from PersonaInfoAdicional i where i.persona.identificacion=?1 and i.estado='A'")
	Long consultaIdInfoAdicional(String identificacion);
	
}
