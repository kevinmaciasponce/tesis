package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Token;

public interface TokenService {
	
	String generaToken(Token token);
	Token findByToken(String token);
	Token findByCuenta(Cuenta id_cuenta);


}
