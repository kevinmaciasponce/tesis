package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Pais;

public interface PaisService {
	
	List<Pais> findAll(String status);	
	String save(Pais pais)throws Exception;
	String deletePais(Long id);
	Pais findById(Long id);
	String consultaPaisPorAbrebiatura(String abreviatura);
	
}
