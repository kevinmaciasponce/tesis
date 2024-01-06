package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PlantillaEmail;
import com.multiplos.cuentas.repository.PlantillaEmailRepository;
import com.multiplos.cuentas.services.PlantillaEmailService;

@Service
public class PlantillaEmailServiceImpl implements PlantillaEmailService {

	private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);
	private PlantillaEmailRepository repository;
	
	@Autowired
	public PlantillaEmailServiceImpl(PlantillaEmailRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public PlantillaEmail consultaPlantillaEmail(String id)throws Exception {
		Optional<PlantillaEmail> plantilla;
		plantilla=repository.findById(id);
		if(plantilla.isEmpty()) {
			throw new Exception("No existe plantilla");
		}
		return plantilla.get();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlantillaEmail> consultaPlantillasEmails() {
		List<PlantillaEmail> list = new ArrayList<>();
		try {
			list = repository.findAll();
		}catch(Exception e) {
			LOG.error("consultaPlantillasEmails: "+e.getMessage());
		}
		return list;
	}


}
