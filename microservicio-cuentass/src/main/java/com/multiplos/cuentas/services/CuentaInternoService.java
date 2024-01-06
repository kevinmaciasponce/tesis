package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.CuentaInterno;

public interface CuentaInternoService {
	
	String save(CuentaInterno usuario);	

	String eliminarRolEmpleado(String idCuenta, Long rol)throws Exception;
	
	
}
