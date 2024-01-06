package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.PersonaInterna;
import com.multiplos.cuentas.models.RolesIntKeys;
import com.multiplos.cuentas.pojo.empleado.EmpleadoCuentaRequest;
import com.multiplos.cuentas.pojo.empleado.EmpleadoRequest;
import com.multiplos.cuentas.pojo.empleado.EmpleadoResponse;
import com.multiplos.cuentas.pojo.empleado.PersonaInternaResponse;
import com.multiplos.cuentas.pojo.empleado.PersonalDetalleResponse;
import com.multiplos.cuentas.pojo.persona.PersonaRequest;

public interface PersonaInternaService {
	
	
	String putEmpleado(EmpleadoRequest empleado) throws Exception;
	Object putCuentaEmpleado(EmpleadoCuentaRequest cuenta) throws Exception;
	String putCuentaEmpleadoRoles(RolesIntKeys key,String userCompose) throws Exception;
	List<PersonaInternaResponse> getEmpleado(PersonaRequest filter)throws Exception;
	
	
	
	List<EmpleadoResponse> findByAnalistasVentas();
	PersonalDetalleResponse consultaEmpleado(String usuario);
	List<PersonalDetalleResponse> consultaEmpleadosPorRol(Long idRol);
	
	
}
