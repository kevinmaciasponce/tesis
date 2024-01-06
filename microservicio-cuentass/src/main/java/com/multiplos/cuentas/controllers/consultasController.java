package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.pojo.transaccion.TransaccionResponse;
import com.multiplos.cuentas.services.PersonaService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.services.impl.GeneraPdfServiceImpl;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class consultasController {
	private SolicitudService solicitudService;
	private ServicesUtils utils;
	private GeneraPdfServiceImpl servicePdf;
	private TransaccionService serviceTransaccion;
	private PersonaService personaService;
	private TablaAmortizacionService tblAmortService;
	@Autowired
	public consultasController(SolicitudService solicitudService,
			ServicesUtils utils,
			GeneraPdfServiceImpl servicePdf,
			TransaccionService serviceTransaccion,
			PersonaService personaService,
			TablaAmortizacionService tblAmortService) {
		this.solicitudService = solicitudService;
		this.personaService= personaService;
		this.tblAmortService= tblAmortService;
		this.serviceTransaccion= serviceTransaccion;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@PostMapping("/consultas/transacciones")
   //@PostMapping("loquesea2")
	public ResponseEntity<List<?>> consultaTransacciones( @RequestParam Long numSol) {
		List<TransaccionResponse> TransaccionResponse = new ArrayList<>();
		List<BadResponse> badResponse= new ArrayList<>();
		try {
		TransaccionResponse = this.serviceTransaccion.consultaPorSolicitud(numSol);
		}catch (Exception e) {
			BadResponse bad = new BadResponse(e.getMessage());
			badResponse.add(bad);
			return ResponseEntity.internalServerError().body(badResponse);}
		return ResponseEntity.ok(TransaccionResponse);
	}


	
	
}
