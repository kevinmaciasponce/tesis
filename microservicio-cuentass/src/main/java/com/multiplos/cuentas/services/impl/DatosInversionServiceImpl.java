package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.repository.DatosInversionRepository;
import com.multiplos.cuentas.services.DatosInversionService;

@Service
public class DatosInversionServiceImpl implements DatosInversionService{

	private static final Logger LOG = LoggerFactory.getLogger(DatosInversionServiceImpl.class);
	private DatosInversionRepository repository;
	
	@Autowired
	public DatosInversionServiceImpl(DatosInversionRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public DatosInversion findByIdDato(Long id) {
		return repository.findByIdDato(id);
	}

	@Override
	@Transactional
	public DatosInversion saveDatoInversion(DatosInversion datoInversion) {
		DatosInversion datoInver = new DatosInversion();
		try{
			datoInver = repository.save(datoInversion);
		}catch(Exception e) {
			LOG.error("saveDatoInversion: Error al guardar datos de inversion "+e.getMessage());
		}
		return datoInver;
	}

	@Override
	@Transactional
	public void updateDatosTablaAmortizacion(boolean tablaAmortizacion, Long idDato) {
		repository.updateDatosTablaAmortizacion(tablaAmortizacion, idDato);
	}

	@Override
	@Transactional
	public void updateDatosFormulario(boolean formulario, Long idDato) {
		repository.updateDatosFormulario(formulario, idDato);
	}

	@Override
	@Transactional
	public void updateDatosDocumentacion(boolean documentacion, Long idDato) {
		repository.updateDatosDocumentacion(documentacion, idDato);
	}

	@Override
	@Transactional
	public void updateDatosPago(boolean pago, Long idDato) {
		repository.updateDatosPago(pago, idDato);
	}

}
