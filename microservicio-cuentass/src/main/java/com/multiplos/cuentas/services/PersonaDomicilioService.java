package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaDomicilio;

public interface PersonaDomicilioService {

	PersonaDomicilio findByIdFormDomicilio(Long id);
	PersonaDomicilio saveDomicilio(PersonaDomicilio domicilio);
}
