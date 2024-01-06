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

import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.services.PaisService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PaisController {
	
	private PaisService paisService;
	private ServicesUtils utilService;

    @Autowired
    public PaisController(PaisService paisService, ServicesUtils utilService) {
        this.paisService = paisService;
        this.utilService = utilService;
    }
    
    @GetMapping(value = "/paises")
    public ResponseEntity<List<Pais>> obtienePaises(){
		List<Pais> paises = new ArrayList<>(); 
		paises = paisService.findAll("A");
		return ResponseEntity.ok(paises);
    }
    
    @GetMapping(value = "/paises/{id}")
    public ResponseEntity<Pais> obtienePais(@PathVariable Long id){
		Pais pais = new Pais(); 
		pais = paisService.findById(id);
		if(pais == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(pais);
    }
    
	@PostMapping(value = "/paises")
    public ResponseEntity<?> creaPais(@Valid @RequestBody Pais pais, BindingResult result)throws Exception{
    	Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return utilService.validar(result);
		}
		String paisResponse = paisService.save(pais);
		mensaje.put("mensaje", paisResponse);
		return ResponseEntity.ok(mensaje);
    }
    
    @DeleteMapping(value = "/paises/{id}")
    public ResponseEntity<?> eliminaPais(@PathVariable Long id){
    	Map<String, Object> mensaje = new HashMap<>();
    	String paisResponse = paisService.deletePais(id);
    	mensaje.put("mensaje", paisResponse);
		return ResponseEntity.ok(mensaje);
    }
    
}
