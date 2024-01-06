package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaEstadoFinanciero;
import com.multiplos.cuentas.repository.PersonaEstadoFinancieroRepository;
import com.multiplos.cuentas.services.PersonaEstadoFinancieroService;

@Service
public class PersonaEstadoFinancieroServiceImpl implements PersonaEstadoFinancieroService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaEstadoFinancieroServiceImpl.class);
	private PersonaEstadoFinancieroRepository repository;
	
	@Autowired
	public PersonaEstadoFinancieroServiceImpl(PersonaEstadoFinancieroRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PersonaEstadoFinanciero findByIdEstFinan(Long id) {
		return repository.findByIdEstFinan(id);
	}

	@Override
	@Transactional
	public PersonaEstadoFinanciero saveEstadoFinanciero(PersonaEstadoFinanciero estadoFinanciero) {
		PersonaEstadoFinanciero persEstFinan = new PersonaEstadoFinanciero();
		try{
			persEstFinan = repository.save(estadoFinanciero);
		}catch(Exception e) {
			LOG.error("saveEstadoFinanciero: Error al guardar datos financieros "+e.getMessage());
		}
		return persEstFinan;
	}

}
