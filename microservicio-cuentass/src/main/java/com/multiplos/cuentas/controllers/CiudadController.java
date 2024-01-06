package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;
import com.multiplos.cuentas.services.CiudadService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class CiudadController {

	private CiudadService ciudadService;
	private ServicesUtils utilService;

    @Autowired
    public CiudadController(CiudadService ciudadService, ServicesUtils utilService) {
        this.ciudadService = ciudadService;
        this.utilService = utilService;
    }
    
    @GetMapping(value = "/ciudades")
    public ResponseEntity<List<CiudadResponse>> obtieneCiudades(){
		List<CiudadResponse> ciudad = new ArrayList<>(); 
		ciudad = ciudadService.findAll("A");
		if(ciudad.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(ciudad);
    }
    
    @GetMapping(value = "/ciudades/{id}")
    public ResponseEntity<CiudadResponse> obtieneCiudad(@PathVariable Long id){
    	CiudadResponse ciudad = new CiudadResponse();
    	ciudad = ciudadService.findById(id);
		if(ciudad == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(ciudad);
    }
    
    @GetMapping(value = "/ciudades/ciudades-pais/{idPais}")
    public ResponseEntity<List<CiudadResponse>> obtieneCiudadesPorPais(@PathVariable Long idPais){
    	List<CiudadResponse> ciudad = new ArrayList<>(); 
    	ciudad = ciudadService.consultaCiudadPorPais(idPais);
		if(ciudad.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(ciudad);
    }
    
	@PostMapping(value = "/ciudades")
    public ResponseEntity<?> creaCiudad(@Valid @RequestBody Ciudad ciudad, BindingResult result)throws Exception{
    	Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return utilService.validar(result);
		}
		String ciudadResponse = ciudadService.save(ciudad);
		mensaje.put("mensaje", ciudadResponse);
		return ResponseEntity.ok(mensaje);
    }
    
    @DeleteMapping(value = "/ciudades/{id}")
    public ResponseEntity<?> eliminaCiudad(@PathVariable Long id){
    	Map<String, Object> mensaje = new HashMap<>();
    	String ciudadResponse = ciudadService.deleteCiudad(id);
    	mensaje.put("mensaje", ciudadResponse);
		return ResponseEntity.ok(mensaje);
    }
}
