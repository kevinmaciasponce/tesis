package com.multiplos.cuentas.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.ConciliacionXls;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.pojo.proyecto.ProyectoPorEstadoResponse;
import com.multiplos.cuentas.pojo.proyecto.UpdatePYSRequest;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class AnalystController {
	private SolicitudService solicitudService;
	private TransaccionService transaccionService;
	private ConciliacionesService csvService;
	private ProyectoService proyectoService;

	public AnalystController(SolicitudService solicitudService, TransaccionService transaccionService,
			ConciliacionesService csvService, ProyectoService proyectoService) {
		this.solicitudService = solicitudService;
		this.transaccionService = transaccionService;
		this.csvService = csvService;
		this.proyectoService = proyectoService;
	}

	@PostMapping("/analista/consultas/transacciones/porConciliar")
	public ResponseEntity<List<?>> consultaSolicitudesPorConfirmar(
			@RequestBody FilterIntSolicitudRequest filterRequest) {
		List<?> listSolicitudResponse = new ArrayList<>();

		listSolicitudResponse = transaccionService.consultaTransaccionesPorConciliar(filterRequest);
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	@GetMapping("/analista/conciliaciones/consultarDatos")
	public ResponseEntity<?> ConsultarDatos() {
		ConciliacionXls formResponse = new ConciliacionXls();
		try {
			formResponse = csvService.consultarFileData();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(formResponse);
	}

	@PostMapping("/analista/conciliaciones/EstadoCuenta/upload")
	public ResponseEntity<?> cargaEstadoDeCuenta(@RequestParam String usuario, @RequestParam MultipartFile file)
			throws Exception {
		String formResponse;
		String contentType = file.getContentType().toLowerCase();
		if (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			throw new Exception("Error archivo no valido, debe cargar un excell");
		}
		try {
			formResponse = csvService.convertFile(usuario, file);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(formResponse));
	}

	@GetMapping("/analista/conciliaciones/conciliarDatos")
	public ResponseEntity<?> ConciliarDatos() {
		String formResponse;
		try {
			formResponse = csvService.conciliarDatos();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(formResponse));
	}

	// @PostMapping("/loquesea2")
	@PostMapping("/analista/conciliaciones/aprobarDatos")
	public ResponseEntity<?> AprobarConciliacion(@RequestParam String usuario) {
		String formResponse;
		try {
			formResponse = csvService.aprobarConciliacion(usuario);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(formResponse));
	}

	///////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	//////// CONSULTAS//////////////////////////////
	/////////////////////////////////////////////////

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/solicitud/aprobadaPorTransferir/porProyecto")
	public ResponseEntity<InversionSolPersonaResponse> solicitudAtfProyecto(@RequestParam String codProyect) {
		InversionSolPersonaResponse solicitudResponse;
		solicitudResponse = solicitudService.consultaDatoSolPersonaPorProyectoYEstado(codProyect, "SATF");
		if (solicitudResponse == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(solicitudResponse);
	}

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/solicitud/enFirma/porProyecto")
	public ResponseEntity<InversionSolPersonaResponse> solicitudEnFirmaProyecto(@RequestParam String codProyect) {
		InversionSolPersonaResponse solicitudResponse;
		solicitudResponse = solicitudService.consultaDatoSolPersonaPorProyectoYEstado(codProyect, "SFC");
		if (solicitudResponse == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(solicitudResponse);
	}
	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/solicitud/liquidada/filter")
	public ResponseEntity<List<?>> consultaSolicitudesLiquidadas(@RequestBody FilterSolicitudRequest filterRequest) {
		List<?> listSolicitudResponse = new ArrayList<>();

		listSolicitudResponse = solicitudService.consultaSolcitudesPapFilter(filterRequest, "Estado", "LQ");
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/solicitud/anulada/filter")
	public ResponseEntity<List<?>> consultaSolicitudesAnuladas(@RequestBody FilterSolicitudRequest filterRequest) {
		List<?> listSolicitudResponse = new ArrayList<>();

		listSolicitudResponse = solicitudService.consultaSolcitudesByFilter(filterRequest, "Estado", "ANU");
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	@PostMapping("analista/consultas/solicitud/por/proyecto/vigente")
	public ResponseEntity<InversionSolPersonaResponse> consultaSolPersonaPorProyecto(@RequestParam String codProyect) {
		InversionSolPersonaResponse solicitudResponse;
		solicitudResponse = solicitudService.consultaDatoSolPersonaPorProyectoYEstado(codProyect, "VG");
		if (solicitudResponse == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(solicitudResponse);
	}

	////////// cuotas//////////////
	// @GetMapping("/loquesea")
	@PostMapping("/solicitud/inversiones/consultas/int/analista/vigente")
	public ResponseEntity<?> consultaCuotas(@RequestBody FilterProyectoRequest filterRequest) {
		List<SolicitudVigenteResponse> response = null;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			response = solicitudService.consultaVigente(filterRequest);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		return ResponseEntity.ok(response);
	}

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/proyectos/aprobadasTF/filter")
	public ResponseEntity<List<proyectosResponse>> consultaProyectoPorEstado_ATF(
			@RequestParam(required = false) String codProyect) {
		List<proyectosResponse> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = this.proyectoService.consultasPorEstado("PATF", codProyect);
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/proyectos/enFirma/filter")
	public ResponseEntity<List<proyectosResponse>> consultaProyectoPorEstado_enFirma(
			@RequestParam(required = false) String codProyect) {
		List<proyectosResponse> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = this.proyectoService.consultasPorEstado("FC", codProyect);
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	// @PostMapping("loquesea2")
	@PostMapping("analista/consultas/proyectos/exitoso/filter")
	public ResponseEntity<List<proyectosResponse>> consultaProyectoPorEstado_exitoso(
			@RequestParam(required = false) String codProyect) {
		List<proyectosResponse> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = this.proyectoService.consultasPorEstado("EXI", codProyect);
		if (listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listSolicitudResponse);
	}

	// @PostMapping("/loquesea2")

	//////////////////////// PROCESOS///////////////////////

	////////////////////////////////////////////////////
	////////////////////////////////////////
	/////////////////////////////////////
	//////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	//////////////////////

	@PostMapping("/solicitud/int/documentos/contratos")
	public ResponseEntity<?> cargaDocumentosxSolicitud(@RequestParam Long numeroSolicitud,
			@RequestParam String usuarioInterno, @RequestParam String observacion,
			@RequestParam MultipartFile datosInversionista, @RequestParam MultipartFile contratoPrenda,
			@RequestParam MultipartFile modeloContrato, @RequestParam MultipartFile pagare,
			@RequestParam MultipartFile tablaAmortizacion, @RequestParam MultipartFile acuerdoUso) {
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		MultipartFile[] file = new MultipartFile[6];
		file[0] = datosInversionista;
		file[1] = contratoPrenda;
		file[2] = modeloContrato;
		file[3] = pagare;
		file[4] = tablaAmortizacion;
		file[5] = acuerdoUso;
		try {
			response = solicitudService.guardarDocumentosxSolicitud(numeroSolicitud, file, observacion, usuarioInterno);
		} catch (Exception e) {
			response = e.getMessage();
		}
		if (response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje", "Sus archivos se han cargado correctamente");
		return ResponseEntity.ok(mensaje);
	}

	@PostMapping("/solicitud/int/proyectos/aprobar")
	public ResponseEntity<?> aprobarProyectoaFirmaContrato(@RequestParam String codProyecto,
			@RequestParam String usuarioInterno) {
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			response = solicitudService.aprobarProyectoaFirmaContrato(codProyecto, usuarioInterno);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		if (response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje", "El proyecto se ha cambiado de estado");
		return ResponseEntity.ok(mensaje);
	}

	//////////////////////////// TRANSFERIR FONDOS AL
	//////////////////////////// PROMOTOR////////////////////////////
	@PostMapping("/analista/procesos/transferirFondos")
	@Transactional
	public ResponseEntity<?> tranferirFondos(@RequestParam MultipartFile file, @RequestParam String CodProyecto,
			@RequestParam String usuario, @RequestParam String observacion
	/* , BindingResult result */) {
		UpdatePYSRequest request = new UpdatePYSRequest();
		Map<String, Object> mensaje = new HashMap<>();
		request.setCodigoProyecto(CodProyecto);
		request.setObservacionProyecto(observacion);
		request.setUsuario(usuario);
		String response;
		request.setSearchSol("SFC");
		/*
		 * if(result.hasErrors()) { return utils.validar(result); }
		 */
		try {
			if (file == null) {
				return ResponseEntity.badRequest().body(new Response("Debe ingresar comprobante de pago"));
			}
			request.setStatusProyect("PATF");
			request.setStatusSol("SATF");
			response = solicitudService.updatePYS2(request, file);

			request.setSearchSol("SATF");
			request.setStatusProyect("EXI");
			request.setStatusSol("VG");
			response = solicitudService.updatePYS2(request, null);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}

		if (!response.contains("actualizado")) {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje", "Sus archivos se han cargado correctamente");
		return ResponseEntity.ok(mensaje);
	}

	// @PostMapping("loquesea2")
	@PostMapping("/analista/procesos/registrarPagos/vigentes")
	public ResponseEntity<?> registraPagoInversionistaVigente(
			@RequestParam Long numSolicitud,
			@RequestParam int cuota,
			@RequestParam String fechaRealizada,
			@RequestParam String usuarioModificacion,
			@RequestParam MultipartFile file) {
		String response = null;
		LocalDate fechaPago;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			fechaPago = LocalDate.parse(fechaRealizada);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse("El formato de la fecha es incorrecto"));
		}
		try {
			response = solicitudService.registraPagoVigente(numSolicitud, cuota, fechaPago, usuarioModificacion, file);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
		if (response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje", "El documento se ha subido correctamente");
		return ResponseEntity.ok(mensaje);
	}

	
	

}
