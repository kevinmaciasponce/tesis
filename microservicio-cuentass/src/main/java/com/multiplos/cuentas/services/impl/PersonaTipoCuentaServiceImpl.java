package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PersonaTipoCuenta;
import com.multiplos.cuentas.repository.PersonaTipoCuentaRepository;
import com.multiplos.cuentas.services.PersonaTipoCuentaService;

@Service
public class PersonaTipoCuentaServiceImpl implements PersonaTipoCuentaService{

	private static final Logger LOG = LoggerFactory.getLogger(PersonaTipoCuentaServiceImpl.class);
	private PersonaTipoCuentaRepository repository;
	
	@Autowired
	public PersonaTipoCuentaServiceImpl(PersonaTipoCuentaRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public PersonaTipoCuenta findByIdPersCuenta(Long id) {
		return repository.findByIdPersCuenta(id);
	}

	@Override

	public PersonaTipoCuenta saveTipoCuenta(PersonaTipoCuenta cuenta) {
		PersonaTipoCuenta persTipoCta = new PersonaTipoCuenta();
		try{
			persTipoCta = repository.save(cuenta);
		}catch(Exception e) {
			LOG.error("saveTipoCuenta: Error al guardar datos de la cuenta "+e.getMessage());
		}
		return persTipoCta;
	}

}
