package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.TipoActividad;
import com.multiplos.cuentas.services.TipoActividadService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class TipoActividadController {

	private TipoActividadService service;
	
	@Autowired
	public TipoActividadController(TipoActividadService service) {
		this.service = service;
	}
	
	@GetMapping("/sector")
    public ResponseEntity<List<TipoActividad>> consultaActividades() {
		List<TipoActividad> listActividad = new ArrayList<>();
		listActividad = service.findAll();
		if(listActividad.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listActividad);
    }
	
	@GetMapping("/sector/{id}")
    public ResponseEntity<TipoActividad> consultaActividad(@PathVariable Long id) {
		TipoActividad calificacion = new TipoActividad();
		calificacion = service.findById(id);
		if(calificacion == null ) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(calificacion);
    }
}
