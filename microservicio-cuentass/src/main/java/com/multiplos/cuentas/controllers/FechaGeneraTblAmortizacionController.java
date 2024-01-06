package com.multiplos.cuentas.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.ResponseBoolean;
import com.multiplos.cuentas.pojo.amortizacion.FechaGenTblAmortRequest;
import com.multiplos.cuentas.services.FechaGeneraTblAmortizacionService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class FechaGeneraTblAmortizacionController {

	private FechaGeneraTblAmortizacionService service;
	private ServicesUtils utilService;
	
	@Autowired
	public FechaGeneraTblAmortizacionController(FechaGeneraTblAmortizacionService service,ServicesUtils utilService) {
		this.service = service;
		this.utilService = utilService;
	}
	
	
	
	//@PostMapping("/loquesea2")
	@PostMapping("generacion/validar/proyecto")
	public ResponseEntity<?> validarProyecto(@RequestParam String codProyect) {
		Boolean response;
		try {	
		response= service.validarProyecto(codProyect);
		}catch (Exception e){return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(new ResponseBoolean(response));
	}
	
	

	@SuppressWarnings("unchecked")
	//@PostMapping(value = "/loquesea2")
	@PostMapping("/generacion/fechaAmortizacion")
    public ResponseEntity<?> creaFechaGeneracionAmortizacion(@Valid @RequestBody FechaGenTblAmortRequest fechaGeneraTblAmortizacion, BindingResult result){
		String response;
		Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return (ResponseEntity<Response>) utilService.validar(result);
		}
    	try {
    	response = service.guardaFechaGeneracionAmortizacion(fechaGeneraTblAmortizacion);
    	}catch(Exception e) {return ResponseEntity.badRequest().body(e.getMessage());}
    	
    	if(response.contains("Error")) {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje",response);
		return ResponseEntity.ok(mensaje);
    }
	
	@SuppressWarnings("unchecked")
	@PutMapping("/generacion/fechaAmortizacion")
    public ResponseEntity<?> actualizaFechaGeneracionAmortizacion(@Valid @RequestBody FechaGenTblAmortRequest fechaGeneraTblAmortizacion, BindingResult result){
		String response;
		Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return (ResponseEntity<Response>) utilService.validar(result);
		}
    	response = service.actualizaFechaGeneracionAmortizacion(fechaGeneraTblAmortizacion);
    	if(response.contains("Error")) {
    		mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
    	mensaje.put("mensaje",response);
		return ResponseEntity.ok(mensaje);
    }
	//@PostMapping(value = "/loquesea2")
	@PostMapping("/generacion/fechaEfectiva")
    public ResponseEntity<?> generaFechaEfectivaAmortizacion(){
		String response;
		Map<String, Object> mensaje = new HashMap<>();
		try {
    	response =service.procesoGeneraTablaAmortizacionMasiva("post");
		}catch (Exception e) {return ResponseEntity.badRequest().body(e.getMessage());}
    	if(response.contains("Error")) {
    		mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
    	mensaje.put("mensaje",response);
		return ResponseEntity.ok(mensaje);
    }
	
	@PutMapping("/generacion/fechaEfectiva")
	public ResponseEntity<?> actualizaFechaEfectivaAmortizacion(){
		String response;
		Map<String, Object> mensaje = new HashMap<>();
		try {
	   	response =service.procesoGeneraTablaAmortizacionMasiva("put");
		}catch (Exception e) {return ResponseEntity.badRequest().body(e.getMessage());}
	   	if(response.contains("Error")) {
	   		mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
	   	mensaje.put("mensaje",response);
		return ResponseEntity.ok(mensaje);
	}
	
}
