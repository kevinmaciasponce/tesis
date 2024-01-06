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

import com.multiplos.cuentas.models.FormaPago;
import com.multiplos.cuentas.services.FormaPagoService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class FormaPagoController {
	
	private FormaPagoService formaPagoService;
	private ServicesUtils utilService;

	@Autowired
	public FormaPagoController(FormaPagoService formaPagoService,ServicesUtils utilService) {
		this.formaPagoService = formaPagoService;
		this.utilService = utilService;
	}
	
	@GetMapping(value = "/formaPagos")
    public ResponseEntity<List<FormaPago>> obtieneFormaPagos(){
		List<FormaPago> formaPago = new ArrayList<>(); 
		formaPago = formaPagoService.findAll("A");
		if(formaPago.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(formaPago);
    }
    
    @GetMapping(value = "/formaPagos/{id}")
    public ResponseEntity<FormaPago> obtieneFormaPagos(@PathVariable Long id){
    	FormaPago formaPago = new FormaPago();
    	formaPago = formaPagoService.consultaFormaPago(id);
		if(formaPago == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(formaPago);
    }
    
    @PostMapping(value = "/formaPagos")
    public ResponseEntity<?> creaFormaPago(@Valid @RequestBody FormaPago formaPago, BindingResult result)throws Exception{
    	Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return utilService.validar(result);
		}
		String response = formaPagoService.save(formaPago);
		mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
    }
    
    @DeleteMapping(value = "/formaPagos/{id}")
    public ResponseEntity<?> eliminaFormaPago(@PathVariable Long id){
    	Map<String, Object> mensaje = new HashMap<>();
    	String response = formaPagoService.delete(id);
    	mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
    }
	

}
