package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaDomicilio;

public interface PersonaDomicilioRepository extends JpaRepository<PersonaDomicilio, Long>{
	
	PersonaDomicilio findByIdDomicilio(Long id);
}
