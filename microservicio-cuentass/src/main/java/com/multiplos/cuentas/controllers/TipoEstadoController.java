package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.services.TipoEstadoService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class TipoEstadoController {

	private TipoEstadoService service;
	
	@Autowired
	public TipoEstadoController(TipoEstadoService service) {
		this.service = service;
	}
	
	@GetMapping("/tipoEstados")
    public ResponseEntity<List<TipoEstado>> consultaTiposEstados() {
		List<TipoEstado> listTipoEstado = new ArrayList<>();
		listTipoEstado = service.findAll();
		if(listTipoEstado.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listTipoEstado);
    }
	
	@GetMapping("/tipoEstados/{id}")
    public ResponseEntity<TipoEstado> consultaTipoEstado(@PathVariable String id) {
		TipoEstado calificacion = new TipoEstado();
		calificacion = service.findById(id);
		if(calificacion == null ) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(calificacion);
    }

}
