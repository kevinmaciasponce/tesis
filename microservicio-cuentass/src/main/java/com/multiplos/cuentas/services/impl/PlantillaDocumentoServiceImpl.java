package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PlantillaDocumento;
import com.multiplos.cuentas.pojo.plantilla.PlantillaDocumentos;
import com.multiplos.cuentas.repository.PlantillaDocumentoRepository;
import com.multiplos.cuentas.services.PlantillaDocumentoService;

@Service
public class PlantillaDocumentoServiceImpl implements PlantillaDocumentoService {

	private static final Logger LOG = LoggerFactory.getLogger(PlantillaDocumentoServiceImpl.class);
	private PlantillaDocumentoRepository repository;
	
	@Autowired
	public PlantillaDocumentoServiceImpl(PlantillaDocumentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PlantillaDocumento consultaPlantillaDocumento(String id) {
		return repository.findByIdPlantilla(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlantillaDocumento> consultaPlantillasDocumentos() {
		List<PlantillaDocumento> list = new ArrayList<>();
		try {
			list = repository.findAll();
		}catch(Exception e) {
			LOG.error("consultaPlantillasDocumentos: "+e.getMessage());
		}
		return list;
	}

	public String extraeJson(String json) {
		PlantillaDocumentos planDoc = new PlantillaDocumentos();
		String item;
		//String body;
		JSONObject jsonObject = new JSONObject(json);
		planDoc.setTitulo(jsonObject.getString("titulo"));
		item = jsonObject.getString("items");
		LOG.info("titulo: "+planDoc.getTitulo());
		LOG.info("item: "+item);
		return item;
		
	}
}
