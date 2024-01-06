package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.FilterPersonaResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;

public interface PersonaService {
	
	String guardaPersona(Persona persona);
	Persona consultaPersonas(String  idCuenta);
	
	Persona findById(String identificacion);
	
	DocIdentificacionResponse consultaDocIdentificacion (String identificacion);
	
	PersonaResponse consultaDatosPersona(String identificacion) throws Exception;
	List<?>consultaFilterPersona() throws Exception;
	boolean existePersona(String identificacion);
}
