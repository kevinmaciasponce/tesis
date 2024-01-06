package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaInfoAdicional;

public interface PersonaInfoAdicionalService {
	
	PersonaInfoAdicional guardaInforAdicional(PersonaInfoAdicional indoAdicional);
	PersonaInfoAdicional consultaPersonaInfoAdicional(String identificacion);
	Long consultaIdInfoAdicional(String identificacion);
}
