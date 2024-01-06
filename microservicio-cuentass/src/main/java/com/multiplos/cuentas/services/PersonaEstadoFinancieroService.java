package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaEstadoFinanciero;

public interface PersonaEstadoFinancieroService {
	PersonaEstadoFinanciero findByIdEstFinan(Long id);
	PersonaEstadoFinanciero saveEstadoFinanciero(PersonaEstadoFinanciero estadoFinanciero);
}
