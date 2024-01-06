package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.pojo.empleado.EmpleadoResponse;
import com.multiplos.cuentas.pojo.empleado.PersonalDetalleResponse;
import com.multiplos.cuentas.services.PersonaInternaService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class EmpleadoController {
	
	private PersonaInternaService empleadoService;

    @Autowired
    public EmpleadoController(PersonaInternaService empleadoService) {
		this.empleadoService = empleadoService;
	}


	@GetMapping(value = "/empleados/ventas")
    public ResponseEntity<List<EmpleadoResponse>> obtieneEmpleadoAnalista(){
		List<EmpleadoResponse> empleado = new ArrayList<>();
		empleado = empleadoService.findByAnalistasVentas();
		if(empleado.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(empleado);
    }
	
	@GetMapping(value = "/empleados/rol/{id}")
    public ResponseEntity<List<PersonalDetalleResponse>> consultaEmpleadosPorRol(@PathVariable Long id){
		List<PersonalDetalleResponse> empleado = new ArrayList<>();
		empleado = empleadoService.consultaEmpleadosPorRol(id);
		if(empleado.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(empleado);
    }

}
