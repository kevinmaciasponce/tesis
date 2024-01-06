package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaDocumento;

public interface PersonaDocumentoRepository extends JpaRepository<PersonaDocumento, Long>{
	
	PersonaDocumento findByIdDocumento(Long id);

}
