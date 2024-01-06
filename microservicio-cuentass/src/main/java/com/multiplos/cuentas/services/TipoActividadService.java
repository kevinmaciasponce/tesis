package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.TipoActividad;

public interface TipoActividadService {

	List<TipoActividad> findAll();
	TipoActividad findById(Long id);
	
}
