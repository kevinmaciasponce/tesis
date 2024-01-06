package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersonaRequest;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.services.PersonaService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PersonaController {
	
	private PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }
    
	@GetMapping("/consultas/filter/persona")
	  public ResponseEntity<?> consultaFilterPersona() {
			List<?>filterResponse = new ArrayList<>();
//			if(result.hasErrors()) {
//				return (ResponseEntity<SolicitudResponse>) utils.validar(result);
//			}
			try {
				filterResponse = this.personaService.consultaFilterPersona();
			}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
	      return ResponseEntity.ok(filterResponse);
	  }
    
    @PostMapping("/consultas/persona")
    public ResponseEntity<?> consultaPersona(@Valid @RequestParam String identificacion) {
		PersonaResponse personaResponse = new PersonaResponse();
//		if(result.hasErrors()) {
//			return (ResponseEntity<SolicitudResponse>) utils.validar(result);
//		}
		try {
		personaResponse = this.personaService.consultaDatosPersona(identificacion);
		}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
        return ResponseEntity.ok(personaResponse);
    }

    @PostMapping(value = "/consultas/persona/documento/identificacion")
    public ResponseEntity<?> consultaDocumentoPersona(@RequestParam String identificacion) {
    	DocIdentificacionResponse persona = null;
		persona = personaService.consultaDocIdentificacion(identificacion);
		if(persona == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(persona);
    }
    
    
//    
//    @PostMapping(value = "/personas")
//    public ResponseEntity<?> consultaPersonas(@RequestBody PersonaRequest request) {
//		PersonaResponse persona = new PersonaResponse();
//		try {
//		persona = personaService.consultaDatosPersona(request.getIdentificacion());
//		}catch(Exception e) {
//			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
//		}
//		if(persona == null) {
//			return ResponseEntity.noContent().build();
//		}
//        return ResponseEntity.ok(persona);
//    }
 
//  @PostMapping(value = "loquesea2")

    
    
    
    
    

}
