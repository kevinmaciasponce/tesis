package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
	
	
	Token findByToken(String token);
	
	
	//@Query( "select t from Token t where t.cuenta=:cuenta ")
	Token findByCuenta(Cuenta cuenta);

}
