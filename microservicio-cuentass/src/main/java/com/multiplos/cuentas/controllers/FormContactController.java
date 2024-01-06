package com.multiplos.cuentas.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.FormContact;
import com.multiplos.cuentas.services.FormContactService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class FormContactController {
	private FormContactService formContactService;
	private ServicesUtils utilService;
	
	
	@Autowired
    public FormContactController(FormContactService formContactService,ServicesUtils utilService) {
		 this.formContactService = formContactService;
		 this.utilService = utilService;
	}
	  @PostMapping("/formcontact")
	    public ResponseEntity<?> save(@Valid @RequestBody FormContact formcontact, BindingResult result){
		  if(result.hasErrors()) {
				return utilService.validar(result);
			}
		  Map<String, Object> mensaje = new HashMap<>();
	    	String Response;
	    	
	    	try {
			 Response = formContactService.save(formcontact);
	    	}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
			mensaje.put("mensaje", Response);
			return ResponseEntity.ok(mensaje);
	    }
	
}
