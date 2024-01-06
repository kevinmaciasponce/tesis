package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.CuentaInterno;

public interface CuentaInternoRepository extends JpaRepository<CuentaInterno, String>{
	
	@Query("select c from CuentaInterno c where c.email=?1 and c.estado='A'")
	CuentaInterno findByCuentaInterna(String email);
}
