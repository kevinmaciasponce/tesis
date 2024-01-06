package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.pojo.parametro.ParametroResponse;

public interface ParametroService {
	
	ParametroResponse findByParametroCodParametro(String codParametro);
	ParametroResponse findByParametroCodParametroThrow(String codParametro)throws Exception;
	List<ParametroResponse> findByParametro(String parametro);
	String consultaParametroDescripcion(String valor);//
}
