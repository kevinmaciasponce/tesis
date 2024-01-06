package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaRepresentanteLegal;
import com.multiplos.cuentas.repository.PersonaRepresentanteLegalRepository;
import com.multiplos.cuentas.services.PersonaRepresentanteLegalService;

@Service
public class PersonaRepresentanteLegalServiceImpl implements PersonaRepresentanteLegalService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaRepresentanteLegalServiceImpl.class);
	private PersonaRepresentanteLegalRepository repository;
	
	@Autowired
	public PersonaRepresentanteLegalServiceImpl(PersonaRepresentanteLegalRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PersonaRepresentanteLegal findByIdRepreLegal(Long id) {
		return repository.findByIdRepreLegal(id);
	}

	@Override
	@Transactional
	public PersonaRepresentanteLegal saveRepresentanteLegal(PersonaRepresentanteLegal representanteLegal) {
		PersonaRepresentanteLegal persRepreLegal = new PersonaRepresentanteLegal();
		try{
			persRepreLegal = repository.save(representanteLegal);
		}catch(Exception e) {
			LOG.error("saveRepresentanteLegal: Error al guardar datos del representante "+e.getMessage());
		}
		return persRepreLegal;
	}

}
