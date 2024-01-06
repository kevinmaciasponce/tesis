package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.PlantillaDocumento;
import com.multiplos.cuentas.services.PlantillaDocumentoService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PlantillaDocumentoController {
	
	private PlantillaDocumentoService plantillaService;
	
	@Autowired
	public PlantillaDocumentoController(PlantillaDocumentoService plantillaService) {
		this.plantillaService = plantillaService;
	}

	@GetMapping(value = "plantilla/documentos")
    public ResponseEntity<List<PlantillaDocumento>> obtienePlantillas(){
		List<PlantillaDocumento> plantilla = new ArrayList<>();
		plantilla = plantillaService.consultaPlantillasDocumentos().stream().filter(p -> p.getEstado().contains("A")).collect(Collectors.toList());
		if(plantilla.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(plantilla);
    }
}
