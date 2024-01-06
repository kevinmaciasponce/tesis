package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.PorcentajeInteresProyecto;

public interface PorcentajeInteresProyectoService {
	
	List<PorcentajeInteresProyecto> findAll();
	List<PorcentajeInteresProyecto> consultaPorcentajesPorProyecto(String codigoProyecto);
	PorcentajeInteresProyecto findById(Long id);
	
}
