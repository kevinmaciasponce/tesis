package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaFirma;

public interface PersonaFirmaRepository extends JpaRepository<PersonaFirma, Long>{
	
	PersonaFirma findByIdFirma(Long id);

}
