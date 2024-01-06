package com.multiplos.cuentas.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.pojo.proyecto.PorcSolAprobadaRequest;
import com.multiplos.cuentas.services.PorcentajeSolicitudAprobadaService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PorcentajeSolicitudAprobadaController {

	private PorcentajeSolicitudAprobadaService service;
	private ServicesUtils utilService;
	
	@Autowired
	public PorcentajeSolicitudAprobadaController(PorcentajeSolicitudAprobadaService service, ServicesUtils utilService) {
		this.service = service;
		this.utilService = utilService;
	}

	
	@PostMapping("/inversiones/int/porcentajeAprobado")
    public ResponseEntity<?> creaPorcentajeAprobado(@Valid @RequestBody PorcSolAprobadaRequest request, BindingResult result){
		String response;
		
		if(result.hasErrors()) {
			return utilService.validar(result);
		}
		
		try {
			response = service.guardaPorcentajeSolAprobada(request);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		
		return ResponseEntity.ok(new Response(response));
    }
}
