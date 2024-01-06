package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaDomicilio;
import com.multiplos.cuentas.repository.PersonaDomicilioRepository;
import com.multiplos.cuentas.services.PersonaDomicilioService;

@Service
public class PersonaDomicilioServiceImpl implements PersonaDomicilioService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaDomicilioServiceImpl.class);
	private PersonaDomicilioRepository repository;
	
	@Autowired
	public PersonaDomicilioServiceImpl(PersonaDomicilioRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public PersonaDomicilio findByIdFormDomicilio(Long id) {
		return repository.findByIdDomicilio(id);
	}

	@Override
	@Transactional
	public PersonaDomicilio saveDomicilio(PersonaDomicilio domicilio) {
		PersonaDomicilio persDomicilio = new PersonaDomicilio();
		try{
			persDomicilio = repository.save(domicilio);
		}catch(Exception e) {
			LOG.error("saveDomicilio: Error al guardar datos del domicilio "+e.getMessage());
		}
		return persDomicilio;
	}

}
