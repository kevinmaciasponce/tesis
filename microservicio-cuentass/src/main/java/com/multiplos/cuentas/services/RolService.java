package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Rol;

public interface RolService {

	List<Rol> findAll();
	
	Rol findByIdRol(Long idRol);
	
	Rol consultaRol(String nombreRol);
}
