package com.multiplos.cuentas.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaInfoAdicional;
import com.multiplos.cuentas.repository.PersonaInfoAdicionalRepository;
import com.multiplos.cuentas.services.PersonaInfoAdicionalService;

@Service
public class PersonaInfoAdicionalServiceImpl implements PersonaInfoAdicionalService {

	private PersonaInfoAdicionalRepository infoAdicionalRepository;
	
	@Autowired
	public PersonaInfoAdicionalServiceImpl(PersonaInfoAdicionalRepository infoAdicionalRepository) {
		this.infoAdicionalRepository = infoAdicionalRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public PersonaInfoAdicional consultaPersonaInfoAdicional(String identificacion) {
		return infoAdicionalRepository.consultaInfoAdicional(identificacion);
	}

	@Override
	@Transactional
	public PersonaInfoAdicional guardaInforAdicional(PersonaInfoAdicional indoAdicional) {
		return infoAdicionalRepository.save(indoAdicional);
	}

	@Override
	@Transactional(readOnly = true)
	public Long consultaIdInfoAdicional(String identificacion) {
		return infoAdicionalRepository.consultaIdInfoAdicional(identificacion);
	}

}
