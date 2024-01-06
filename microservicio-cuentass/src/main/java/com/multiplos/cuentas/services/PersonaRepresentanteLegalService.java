package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaRepresentanteLegal;

public interface PersonaRepresentanteLegalService {
	PersonaRepresentanteLegal findByIdRepreLegal(Long id);
	PersonaRepresentanteLegal saveRepresentanteLegal(PersonaRepresentanteLegal representanteLegal);
}
