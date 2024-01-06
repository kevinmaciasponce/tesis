package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Calificacion;
import com.multiplos.cuentas.services.CalificacionService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class CalificacionController {
	
	private CalificacionService service;
	
	@Autowired
	public CalificacionController(CalificacionService service) {
		this.service = service;
	}

	@GetMapping("/calificaciones")
    public ResponseEntity<List<Calificacion>> consultaCalificaciones() {
		List<Calificacion> listCalificacion = new ArrayList<>();
		listCalificacion = service.findAll();
		if(listCalificacion.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listCalificacion);
    }
	
	@GetMapping("/calificaciones/{id}")
    public ResponseEntity<Calificacion> consultaCalificacion(@PathVariable Long id) {
		Calificacion calificacion = new Calificacion();
		calificacion = service.findById(id);
		if(calificacion == null ) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(calificacion);
    }

}
