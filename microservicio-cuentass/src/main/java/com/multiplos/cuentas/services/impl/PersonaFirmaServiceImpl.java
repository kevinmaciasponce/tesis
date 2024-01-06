package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaFirma;
import com.multiplos.cuentas.repository.PersonaFirmaRepository;
import com.multiplos.cuentas.services.PersonaFirmaService;

@Service
public class PersonaFirmaServiceImpl implements PersonaFirmaService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaFirmaServiceImpl.class);
	private PersonaFirmaRepository repository;
	
	@Autowired
	public PersonaFirmaServiceImpl(PersonaFirmaRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PersonaFirma findByIdFirma(Long id) {
		return repository.findByIdFirma(id);
	}

	@Override
	@Transactional
	public PersonaFirma savePersonaFirma(PersonaFirma personaFirma) {
		PersonaFirma persFirma = new PersonaFirma();
		try{
			persFirma = repository.save(personaFirma);
		}catch(Exception e) {
			LOG.error("savePersonaFirma: Error al guardar datos de la firma autorizada "+e.getMessage());
		}
		return persFirma;
	}

}
