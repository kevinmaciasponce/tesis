package com.multiplos.cuentas.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.formulario.FormJurRequest;
import com.multiplos.cuentas.pojo.formulario.FormNatRequest;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersonaRequest;
import com.multiplos.cuentas.pojo.proyecto.UpdatePYSRequest;
import com.multiplos.cuentas.pojo.proyecto.PorcSolAprobadaRequest;
import com.multiplos.cuentas.pojo.proyecto.ProyectoPorEstadoResponse;
import com.multiplos.cuentas.pojo.solicitud.NumeroSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocPagoRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudGetRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.CantidadSolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionAprobTransFondoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerenciaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionHistorialEstado;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.inversionResponse;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.services.impl.GeneraPdfServiceImpl;
import com.multiplos.cuentas.services.impl.SolicitudServiceImpl;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class SolicitudController {
	private static final Logger LOG = LoggerFactory.getLogger(SolicitudController.class);
	private SolicitudService solicitudService;
	private ServicesUtils utils;
	private GeneraPdfServiceImpl servicePdf;
	private TransaccionService serviceTransaccion;
	@Autowired
	public SolicitudController(SolicitudService solicitudService,
			ServicesUtils utils,
			GeneraPdfServiceImpl servicePdf,
			TransaccionService serviceTransaccion) {
		this.solicitudService = solicitudService;
		this.utils = utils;
		this.serviceTransaccion= serviceTransaccion;
		this.servicePdf = servicePdf;
	}
	
//	@PostMapping("/solicitud")
//    public ResponseEntity<?> consultaSolicitudes(@RequestBody SolicitudGetRequest solicitudRequest) {
//		List<SolicitudResponse> listSolicitudResponse = new ArrayList<>();
//		Map<String, Object> mensaje = new HashMap<>();
//		if(solicitudRequest.getIdentificacion()!=null && solicitudRequest.getEstado()!=null) {
//			listSolicitudResponse = solicitudService.consultaSolicitudesPorPersonaYEstado(solicitudRequest.getIdentificacion(),solicitudRequest.getEstado());
//			if(listSolicitudResponse.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//		}else {
//			mensaje.put("error", "Los campos identificacion y estado no deben ser nulos");
//			return ResponseEntity.badRequest().body(mensaje);
//		}
//		
//        return ResponseEntity.ok(listSolicitudResponse);
//    }
	
//	@PostMapping("/solicitud/solicitud-proyecto")
//    public ResponseEntity<?> consultaSolicitudPorProyecto(@Valid @RequestBody SolicitudGetRequest solicitudRequest, BindingResult result) {
//		List<SolicitudResponse> solicitudes = new ArrayList<>();
//		Map<String, Object> mensaje = new HashMap<>();
//		if(solicitudRequest.getCodigoProyecto() != null) {
//			solicitudes = solicitudService.consultaSolicitudPorProyecto(solicitudRequest.getCodigoProyecto());
//			if(solicitudes.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//		}else {
//			mensaje.put("error", "El campo codigoProyecto no deben ser nulo");
//			return ResponseEntity.badRequest().body(mensaje);
//		}
//		
//        return ResponseEntity.ok(solicitudes);
//    }
	


	
	@PutMapping("/solicitud/formularios/nat")
    public ResponseEntity<?> actualizaFormularioNat(@RequestBody FormNatRequest request) {
    	String response = null;
    	Map<String, Object> mensaje = new HashMap<>();
    	try {
    	response = solicitudService.guardaPersonaInfoAdicional(request,null,"update");
    	}catch (Exception e) {
    		return ResponseEntity.badRequest().body(new BadResponse(response,e.getMessage()));	
    	}
    	if(response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(new BadResponse(response));	
		}
		mensaje.put("mensaje", "Actualizado exitosamente.");
		
        return ResponseEntity.ok(mensaje);
    }
	
	@PostMapping("/solicitud/formularios/jur")
    public ResponseEntity<?> creaFormularioJur(@Valid @RequestBody FormJurRequest request, BindingResult result) {
		String formResponse = null;
		Map<String, Object> mensaje = new HashMap<>();
	    
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		
		formResponse = solicitudService.guardaPersonaInfoAdicional(null,request,null);
		if(formResponse != "ok") {
			mensaje.put("error", formResponse);
			return ResponseEntity.badRequest().body(mensaje);
		}
		mensaje.put("mensaje", "Guardado exitosamente.");
		
        return ResponseEntity.ok(mensaje);
    }
	
	@PutMapping("/solicitud/formularios/jur")
    public ResponseEntity<?> actualizaFormularioJur(@RequestBody FormJurRequest request) {
    	String response = null;
    	Map<String, Object> mensaje = new HashMap<>();
    	
    	response = solicitudService.guardaPersonaInfoAdicional(null,request,"update");
		if(response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);	
		}
		mensaje.put("mensaje", "Actualizado exitosamente.");
		
        return ResponseEntity.ok(mensaje);
    }



	
	
	

	
//	@GetMapping("/loquesea")
    public ResponseEntity<?> aprobarProyectoaFirmaContrato2() {
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			 
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		if(response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);	
		}
		mensaje.put("mensaje", "El proyecto se ha cambiado de estado");
        return ResponseEntity.ok(mensaje);
    }
	    
	    
	//@GetMapping("/loquesea")
	

	
	

	

	

	

	//@PostMapping("/loquesea2")

	
	@GetMapping("/solicitud/formularios/documentos/licitudFondo/{identificacion}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String identificacion) throws Exception {
        byte[] pdfReport = servicePdf.getLicitudFondoPDF(identificacion).toByteArray();

        ByteArrayInputStream inStream = new ByteArrayInputStream(pdfReport);   
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=licitudFondo.pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inStream));
    }
	
	@GetMapping("/solicitud/formularios/documentos/licitudFondo2/{identificacion}")
    public void downloadFile(HttpServletResponse response, @PathVariable String identificacion) throws Exception {
        byte[] pdfReport = servicePdf.getLicitudFondoPDF(identificacion).toByteArray();

        String mimeType =  "application/pdf";
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "licitudFondo.pdf"));

        response.setContentLength(pdfReport.length);

        ByteArrayInputStream inStream = new ByteArrayInputStream(pdfReport);

        FileCopyUtils.copy(inStream, response.getOutputStream());
    }
	
	
	
	@PostMapping("/solicitud/inversiones/filter/historialEstados")
    public ResponseEntity<List<InversionHistorialEstado>> consultaSolicitudHistorialEstado(@RequestBody NumeroSolicitudRequest request) {
		List<InversionHistorialEstado> listEstadoResponse = new ArrayList<>();
		listEstadoResponse = solicitudService.consultaHistorialEstadosSolicitud(request.getNumeroSolicitud());
		if(listEstadoResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listEstadoResponse);
    }
	
	@PostMapping("/solicitud/inversiones/filter/dashboard")
    public ResponseEntity<?> consultaCantidadSolicitudes(@Valid @RequestBody PersonaRequest request, BindingResult result) {
		List<CantidadSolicitudResponse> listSolicitudResponse = new ArrayList<>();
		Map<String, Object> mensaje = new HashMap<>();
		
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		
		listSolicitudResponse = solicitudService.consultaCantidadSolicitudes(request.getIdentificacion(),request.getTipoCliente());
		if(listSolicitudResponse.isEmpty()) {
			mensaje.put("mensaje", "No se encontraron datos del inversionista.");
			return ResponseEntity.badRequest().body(mensaje);
		}		
        return ResponseEntity.ok(listSolicitudResponse);
    }
	
	//SERVICIOS PARA ANALISTA
	//------------------------------------------------------------
		//-------------------------------------------------------------
		//-----------------------------------------------------*-------//------------------------------------------------------------
	//-------------------------------------------------------------
	//-----------------------------------------------------*-------//------------------------------------------------------------
	//-------------------------------------------------------------
	//-----------------------------------------------------*-------//------------------------------------------------------------
	//-------------------------------------------------------------
	//-----------------------------------------------------*-------//------------------------------------------------------------
	//-------------------------------------------------------------
	//-----------------------------------------------------*-------
	

	

	

	
	@PostMapping("/solicitud/inversiones/consultas/int/analista/aprobada")
    public ResponseEntity<List<InversionAprobTransFondoResponse>> consultaSolicitudesAprobTransFondos(@RequestBody FilterProyectoRequest filterRequest) {
		List<InversionAprobTransFondoResponse> listSolicitudResponse = new ArrayList<>();
		
		listSolicitudResponse = solicitudService.consultaSolicitudesAprobTransFondos(filterRequest);
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	
	//kevin david
	@PostMapping("/solicitud/inversiones/consultas/int/analista/firmaContrato")
    public ResponseEntity<List<ProyectoPorEstadoResponse>> consultaProyectoPorEstado(@RequestBody FilterProyectoRequest filterRequest) {
		List<ProyectoPorEstadoResponse> listSolicitudResponse = new ArrayList<>();
		
		listSolicitudResponse = solicitudService.consultaSolicitudesAgrupadas(filterRequest,null,"SFC");
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	

	
	

	
	
	
	//-----------------------PROCESOS-------------------------------
	
	
	
	
	

	
	@PutMapping("/solicitud/inversiones/procesos/int/analista/actualizarTransation")
	@Transactional
	 public ResponseEntity<?> actualizarTransation( 
			 @RequestParam String usuario, 
			 @RequestParam Long numSolicitud,
			 @RequestParam(required= false) String numComprobante,
			 @RequestParam(required= false) String fecha,
			 @RequestParam(required= false) String monto,
			 @RequestParam(required= false) String observacion){
		String response=null;
		try {
			if(monto!=null) {
			List<Long> lisol = new ArrayList<>();
			lisol.add(numSolicitud);
			solicitudService.actualizaEstadoSolicitudes(lisol, new TipoEstado("PAP"), usuario, observacion);	
			}
			response = this.serviceTransaccion.actualizarTransaccion(usuario,numSolicitud, numComprobante, fecha,monto,observacion);
			
			
			}catch (Exception e) {
			LOG.warn(""+e);
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(new Response(response));	
	}
	
	//SERVICIOS PARA GERENTE------------------------------------------------------------------
	//------------------------------------------------------------
	//-------------------------------------------------------------
	//-----------------------------------------------------*-------
	//------------------------------------------------------------
		//-------------------------------------------------------------
		//-----------------------------------------------------*-------
	//------------------------------------------------------------
		//-------------------------------------------------------------
		//-----------------------------------------------------*-------
	//------------------------------------------------------------
		//-------------------------------------------------------------
		//-----------------------------------------------------*-------
	


	
	
	
	//AUN NOO SE USA
	@PostMapping("/solicitud/inversiones/consultas/int/firmaDeContrato")
    public ResponseEntity<?> firmarContrato(@Valid @RequestParam UpdatePYSRequest request, BindingResult result){
		String response;
		request.setSearchSol("SFC");
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		try {
			request.setStatusProyect("PTF");
			request.setStatusSol("STF");
			response = solicitudService.updatePYS(request);	
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
    }

	
	
	


	
	/*@PostMapping("/solicitud/inversiones/envioEmail")//solo pruebas, luego borrar
	public ResponseEntity<Void> enviaEmailPruebas() {
		solicitudService.enviaEmailSolicitudesPorEstado("BO");
		return new ResponseEntity<>(HttpStatus.OK);
    }*/
	
}
