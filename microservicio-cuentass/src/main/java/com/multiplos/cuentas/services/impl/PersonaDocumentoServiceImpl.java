package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaDocumento;
import com.multiplos.cuentas.repository.PersonaDocumentoRepository;
import com.multiplos.cuentas.services.PersonaDocumentoService;

@Service
public class PersonaDocumentoServiceImpl implements PersonaDocumentoService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaDocumentoServiceImpl.class);
	private PersonaDocumentoRepository repository;
	
	@Autowired
	public PersonaDocumentoServiceImpl(PersonaDocumentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public PersonaDocumento findByIdDocumento(Long id) {
		return repository.findByIdDocumento(id);
	}

	@Override
	@Transactional
	public PersonaDocumento saveDocumento(PersonaDocumento documento) {
		PersonaDocumento persDocumento = new PersonaDocumento();
		try{
			persDocumento = repository.save(documento);
		}catch(Exception e) {
			LOG.error("PersonaDocumentoServiceImpl: Error al guardar datos del documento "+e.getMessage());
		}
		return persDocumento;
	}

}
