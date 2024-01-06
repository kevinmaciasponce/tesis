package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaFirma;

public interface PersonaFirmaService {
	PersonaFirma findByIdFirma(Long id);
	PersonaFirma savePersonaFirma(PersonaFirma personaFirma);
}
