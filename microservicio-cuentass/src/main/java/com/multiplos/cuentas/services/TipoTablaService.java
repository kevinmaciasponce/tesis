package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.TipoTabla;

public interface TipoTablaService {

	List<TipoTabla> findAll();
	TipoTabla findById(Long id);
	
}
