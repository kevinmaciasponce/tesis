package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Calificacion;

public interface CalificacionService {

	List<Calificacion> findAll();
	Calificacion findById(Long id);
}
