package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerenciaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.services.impl.SolicitudServiceImpl;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ManagerOperController {
	private SolicitudService solicitudService;
	private TransaccionService transaccionService;
	private ConciliacionesService csvService;
	private ProyectoService proyectoService;
	private static final Logger LOG = LoggerFactory.getLogger(SolicitudServiceImpl.class);

	public ManagerOperController(SolicitudService solicitudService, TransaccionService transaccionService,
			ConciliacionesService csvService, ProyectoService proyectoService) {
		this.solicitudService = solicitudService;
		this.transaccionService = transaccionService;
		this.csvService = csvService;
		this.proyectoService = proyectoService;
	}
	//@PostMapping("loquesea2")
	@PostMapping("managerOper/consultas/solicitud/enRevision/filter")
	public ResponseEntity<List<?>> consultaSolicitudesEnRevision(@RequestBody FilterSolicitudRequest filterRequest) {
		List<?> listSolicitudResponse = new ArrayList<>();

		listSolicitudResponse = solicitudService.consultaSolcitudesPapFilter(filterRequest, "Estado", "PAP");
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	
	 @PostMapping("managerOper/procesos/solicitud/aprobar/filter")
	public ResponseEntity<?> aprobarSolicitudesEnRevision(@RequestParam Long numSol,@RequestParam String usuario) {

		String response = null;
		try {
			response = this.solicitudService.aprobarSolicitudPap(numSol, usuario, "estados", "PC");
			LOG.warn("existe n"+response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}

		return ResponseEntity.ok(new Response(response));
	}
	 
	// @PostMapping("loquesea2")
	@PostMapping("managerOper/procesos/solicitud/anular/filter")
	public ResponseEntity<?> anularSolicitudesEnRevision(@RequestParam Long numSol,@RequestParam String usuario) {

		String response = null;
		try {
			response = this.solicitudService.anularSolicitudPap(numSol, usuario);
			LOG.warn("existe n"+response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}

		return ResponseEntity.ok(new Response(response));
	}
}
