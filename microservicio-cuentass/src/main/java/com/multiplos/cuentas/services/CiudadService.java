package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;

public interface CiudadService {
	
	List<CiudadResponse> findAll(String estatus);
	List<CiudadResponse> consultaCiudadPorPais(Long idPais);
	String save(Ciudad ciudad)throws Exception;
	String deleteCiudad(Long id);
	CiudadResponse findById(Long id);

}
