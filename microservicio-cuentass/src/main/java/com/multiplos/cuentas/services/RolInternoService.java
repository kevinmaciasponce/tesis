package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.pojo.empleado.RolInternoResponse;

public interface RolInternoService {

	List<RolInt> findAll();
	
	RolInt findByIdRol(Long idRol);
	
	RolInt consultaRol(String nombreRol);
	
	List<RolInternoResponse> consultarRolesInternos();
	List<RolInternoResponse> consultarRolesInternosByEmpleado(String idCuenta);
	String eliminarRolEmpleado(String cuenta, Long Rol);
}
