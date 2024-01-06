package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaDocumento;

public interface PersonaDocumentoService {

	PersonaDocumento findByIdDocumento(Long id);
	PersonaDocumento saveDocumento(PersonaDocumento documento);
	
}
