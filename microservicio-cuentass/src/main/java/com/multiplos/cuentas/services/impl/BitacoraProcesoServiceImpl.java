package com.multiplos.cuentas.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.BitacoraProceso;
import com.multiplos.cuentas.repository.BitacoraProcesoRepository;
import com.multiplos.cuentas.services.BitacoraProcesoService;

@Service
public class BitacoraProcesoServiceImpl implements BitacoraProcesoService {

	private static final Logger LOG = LoggerFactory.getLogger(BitacoraProcesoServiceImpl.class);
	
	private BitacoraProcesoRepository repository;
	
	@Autowired
	public BitacoraProcesoServiceImpl(BitacoraProcesoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public void guardaBitacora(BitacoraProceso bitacora) {
		try {
			repository.save(bitacora);
		}catch(Exception e) {
			LOG.error("Error al guardar bitacora ".concat(bitacora.getProceso()).concat(e.getMessage()));
		}
	}
	
	

	@Override
	@Transactional(readOnly = true)
	public BitacoraProceso consultaBitacoraPorProcesoAndTipo(String proceso, String tipo) {
		return repository.consultaBitacoraPorProcesoAndTipo(proceso, tipo);
	}

	@Override
	@Transactional
	public void guardaBitacoraAll(List<BitacoraProceso> bitacora) {
		try {
			repository.saveAll(bitacora);
		}catch(Exception e) {
			LOG.error("Error al guardar bitacora All ".concat(e.getMessage()));
		}
	}

}
