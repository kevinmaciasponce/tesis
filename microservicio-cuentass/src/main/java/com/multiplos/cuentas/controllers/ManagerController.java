package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.auspicios.services.impl.AuspiciosServiceImpl;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerenciaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ManagerController {
	private static final Logger LOG = LoggerFactory.getLogger(ManagerController.class);
	private SolicitudService solicitudService;
	private TransaccionService transaccionService;
	private ConciliacionesService csvService;
	private ProyectoService proyectoService;
	private CuentaService cuentaService;
	public ManagerController(SolicitudService solicitudService,
			TransaccionService transaccionService,
			ConciliacionesService csvService,
			ProyectoService proyectoService,
			 CuentaService cuentaService) {
		this.cuentaService= cuentaService;
		this.solicitudService= solicitudService;
		this.transaccionService=transaccionService;
		this.csvService= csvService;
		this.proyectoService= proyectoService;}

	//@PostMapping("loquesea2")
	@PostMapping("manager/consultas/proyectos/enTransito/filter")
    public ResponseEntity<List<InversionEnTransitoGerenciaResponse>> consultaSolicitudesEnTransitoGerencia(@RequestBody FilterProyectoRequest filterRequest) {
		List<InversionEnTransitoGerenciaResponse> listSolicitudResponse = new ArrayList<>();
		
		listSolicitudResponse = solicitudService.consultaSolicitudesEnTransitoGerencia(filterRequest);
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	@PostMapping("manager/consultas/proyectos/exitoso/filter")
	public ResponseEntity<List<proyectosResponse>> consultaProyectoPorEstado_exitoso(@RequestBody FilterProyectoRequest filter ) {
		List<proyectosResponse> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = this.proyectoService.consultasPorEstado2("EXI",filter);
			if(listSolicitudResponse.isEmpty()) {
				return ResponseEntity.noContent().build();
				}
				return ResponseEntity.ok(listSolicitudResponse);
			}
	
	//@PostMapping("loquesea2")
	@PostMapping("manager/consultas/solicitud/enTransito/porProyecto")
    public ResponseEntity<InversionSolPersonaResponse> solicitudEnFirmaProyecto(@RequestParam String codProyect) {
		InversionSolPersonaResponse solicitudResponse;
		solicitudResponse = solicitudService.consultaDatoSolPersonaPorProyectoYEstado(codProyect,"TN");
		if(solicitudResponse == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(solicitudResponse);
    }
	
	@PostMapping("manager/consultas/solicitud/vigente/porProyecto")
    public ResponseEntity<InversionSolPersonaResponse> consultaSolPersonaPorProyecto(@RequestParam String codProyect) {
		InversionSolPersonaResponse solicitudResponse;
		solicitudResponse = solicitudService.consultaDatoSolPersonaPorProyectoYEstado(codProyect,"VG");
		if(solicitudResponse == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(solicitudResponse);
    }
	
	
	@SuppressWarnings("unchecked")
	@PostMapping("/solicitud/manager/filter/vigente")
    public ResponseEntity<List<?>> consultaSolicitudesPorCampoGerenteGeneral( @RequestBody FilterSolicitudRequest filterRequest, BindingResult result) {
		List<?> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = solicitudService.consultaSolcitudesByFilter(filterRequest,"estados","VG");
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	
	@PostMapping("public/proceso/asignar/representante")
    public ResponseEntity<?> AsiganarRoll(@RequestParam String cuenta) throws NumberFormatException, Exception {
		Object response=null;
		Long roll= (long)10;
		try {
		response =   cuentaService.AsignarRoll(cuenta, roll);
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(c.getMessage());
		}catch(NumberFormatException c) {
			return ResponseEntity.internalServerError().body("Error formato de roll no valido");
		}catch(Exception c) {
			LOG.error(c.getMessage());
			return ResponseEntity.internalServerError().body("Ha ocurrido un error, intente mas tarde");
		}
		if(response == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(response);
    }
	
	@PostMapping("public/proceso/asignar/roll/setear")
    public ResponseEntity<?> procesoparasetearroles() throws NumberFormatException, Exception {
		Object response=null;
		
		try {
		response =   cuentaService.setearRol();
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(c.getMessage());
		}catch(NumberFormatException c) {
			return ResponseEntity.internalServerError().body("Error formato de roll no valido");
		}catch(Exception c) {
			LOG.error(c.getMessage());
			return ResponseEntity.internalServerError().body("Ha ocurrido un error, intente mas tarde");
		}
		if(response == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(response);
    }
	
	

}


