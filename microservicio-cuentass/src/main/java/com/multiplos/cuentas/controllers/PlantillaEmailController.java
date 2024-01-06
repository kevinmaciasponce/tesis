package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.PlantillaEmail;
import com.multiplos.cuentas.services.PlantillaEmailService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PlantillaEmailController {
	
	private PlantillaEmailService plantillaService;
	
	@Autowired
	public PlantillaEmailController(PlantillaEmailService plantillaService) {
		this.plantillaService = plantillaService;
	}

	@GetMapping(value = "plantilla/emails")
    public ResponseEntity<List<PlantillaEmail>> obtienePlantillas(){
		List<PlantillaEmail> plantilla = new ArrayList<>();
		plantilla = plantillaService.consultaPlantillasEmails().stream().filter(p -> p.getEstado().contains("A")).collect(Collectors.toList());
		if(plantilla.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(plantilla);
    }

	
}
