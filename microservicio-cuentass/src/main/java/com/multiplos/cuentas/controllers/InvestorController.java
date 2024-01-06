package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse.SolicitudResponseBuilder;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse;
import com.multiplos.cuentas.pojo.transaccion.TransaccionResponse;
import com.multiplos.cuentas.reportes.ReportePorAnio;
import com.multiplos.cuentas.services.PersonaService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.services.impl.GeneraPdfServiceImpl;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class InvestorController {

	
	private SolicitudService solicitudService;
	private ServicesUtils utils;
	private GeneraPdfServiceImpl servicePdf;
	private TransaccionService serviceTransaccion;
	private PersonaService personaService;
	private TablaAmortizacionService tblAmortService;
	@Autowired
	public InvestorController(SolicitudService solicitudService,
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
	
	////////////////////////////////////FLUJO NUEVA INVERSION ////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////// FLUJO PARA CREAR SOLICITUDES 
	@PostMapping(value = "/amortizacion")
    public ResponseEntity<?> creaSolicitudYTblAmortizacion(@Valid @RequestBody SolicitudRequest solicitudRequest, BindingResult result) {
		//AmortizacionResponse solicitudCreada = new AmortizacionResponse();
			   String  solicitudCreada;
			   HashMap<String,String> rs = new HashMap();
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		try {
			solicitudCreada = tblAmortService.creaSolicitudYTablaAmortizacion(solicitudRequest);
			rs.put("numeroSolicitud", solicitudCreada);
		}catch (CuentaException e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		
		catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		
        return ResponseEntity.ok(rs);
    }
	
	
	///////////////////////GUARDA CEDULA///////////////////////
	//////////////////////////////////////////////////////////
	@PostMapping("/investor/formularios/documentos/cedula")
    public ResponseEntity<?> cargaDocumentoCedula(@RequestParam String solicitud, @RequestParam MultipartFile file, @RequestParam MultipartFile filepost) {
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			response = solicitudService.guardaDocumentoIdentificacion(solicitud,file,filepost);
			
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		if(response != "ok") {
			mensaje.put("error", response);
			return ResponseEntity.badRequest().body(mensaje);	
		}
		mensaje.put("mensaje", "Documentos cargados correctamente");
        return ResponseEntity.ok(mensaje);
    }
	///////////////////// ACTUALIZA CEDULA//////////////////////
	/////////////////////////////////////////////////////////////

	@PutMapping("/investor/formularios/documentos/cedula")
    public ResponseEntity<?> actualizaDocumentoCedula(@RequestParam String solicitud, @RequestParam MultipartFile file, @RequestParam MultipartFile filepost) {
		String formResponse ="";
		Map<String, Object> mensaje = new HashMap<>();
		try {
			formResponse = solicitudService.actualizaDocumentoFormulario(solicitud,file,filepost);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
		if(formResponse != "ok") {
			mensaje.put("error", formResponse);
			return ResponseEntity.badRequest().body(mensaje);	
		}
		mensaje.put("mensaje", "Documentos cargados correctamente");
        return ResponseEntity.ok(mensaje);
		
    }
	
	
///////////////////////GUARDA TRANSACCION///////////////////////
//////////////////////////////////////////////////////////
	
	@PostMapping("/investor/formularios/documentos/deposito")
    public ResponseEntity<?> cargaDocumentoPago(@RequestParam String solicitud, @RequestParam MultipartFile file) throws Exception {
		String formResponse ;
		try {
			formResponse = solicitudService.guardaDocumentoComprobantePago(solicitud,file,"DEP");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
        return ResponseEntity.ok(new Response(formResponse));
    }
	
///////////////////////ACTUALIZA TRANSACCION///////////////////////
//////////////////////////////////////////////////////////
	@PutMapping("/investor/formularios/documentos/deposito")
  public ResponseEntity<?> actualizaDocumentoPago(@RequestParam String solicitud, @RequestParam MultipartFile file) {
		DocIdentificacionResponse formResponse = new DocIdentificacionResponse();
		try {
			formResponse = solicitudService.actualizaDocumentoComprobantePago(solicitud,file,"DEP");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
      return ResponseEntity.ok(formResponse);
  }
	

	

	
	
	//CONTINUAR SOLICITUD EN PROCESO
	//@PostMapping("/loquesea2")
	@PostMapping("/solicitud/inversiones/continuarProceso")
    public ResponseEntity<?> consultaSolicitud( @RequestParam Long numSol) {
		FormDatosIngresadoResponse datos = null;

		datos = solicitudService.continuarProceso(numSol);
		if(datos == null) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(datos);
    }
	///DATOS DEL INVERSIONISTA SI EXISTEN
	
	//@PostMapping("loquesea2")

	
	
	
	///////CONSULTA EL DEPOSITO O TRANSFERENCIA//////////////////
	//@PostMapping("loquesea2")
	@PostMapping("/investor/consultas/formularios/documentos/deposito")
    public ResponseEntity<?> consultarDocumentoPago(@Valid @RequestParam Long solicitud) {
		List<Transaccion>  formResponse = new ArrayList<>();
		try {
			formResponse = solicitudService.consultarDocumentoComprobantePago(solicitud);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
        return ResponseEntity.ok(formResponse);
    }
	
	
	
	/////////////////////////////////////////CONSULTAS DEL FLUJO /////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@PostMapping("/solicitud/inversiones/filter/enProceso")
//@PostMapping("/loquesea2")
    public ResponseEntity<List<InversionEnProcesoResponse>> consultaSolicitudesEnProceso(@Valid @RequestBody FilterSolicitudRequest filterRequest, BindingResult result) {
		List<InversionEnProcesoResponse> listSolicitudResponse = new ArrayList<>();
		if(result.hasErrors()) {
			return (ResponseEntity<List<InversionEnProcesoResponse>>) utils.validar(result);
		}
		
		listSolicitudResponse = solicitudService.consultaSolcitudesEnProceso(filterRequest);
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	
	@SuppressWarnings("unchecked")
	@PostMapping("/solicitud/inversiones/filter/porConfirmar")
    public ResponseEntity<List<InversionPorConfirmarResponse>> consultaSolicitudesPorConfirmar(@Valid @RequestBody FilterSolicitudRequest filterRequest, BindingResult result) {
		List<InversionPorConfirmarResponse> listSolicitudResponse = new ArrayList<>();
		
		if(result.hasErrors()) {
			return (ResponseEntity<List<InversionPorConfirmarResponse>>) utils.validar(result);
		}
		
		listSolicitudResponse = solicitudService.consultaSolcitudesPorConfirmar(filterRequest);
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	@SuppressWarnings("unchecked")
	@PostMapping("/solicitud/inversiones/filter/enTransito")
    public ResponseEntity<List<InversionEnTransitoResponse>> consultaSolicitudesEnTransito(@Valid @RequestBody FilterSolicitudRequest filterRequest, BindingResult result) {
		List<InversionEnTransitoResponse> listSolicitudResponse = new ArrayList<>();
		
		if(result.hasErrors()) {
			return (ResponseEntity<List<InversionEnTransitoResponse>>) utils.validar(result);
		}
		
		listSolicitudResponse = solicitudService.consultaSolEnTransitoInversionista(filterRequest);
		
		
		
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	//@PostMapping("loquesea2")
	@SuppressWarnings("unchecked")
	@PostMapping("/solicitud/inversiones/filter/vigente")
    public ResponseEntity<List<?>> consultaSolicitudesPorFiltro( @RequestBody FilterSolicitudRequest filterRequest, BindingResult result) {
		List<?> listSolicitudResponse = new ArrayList<>();
		if(result.hasErrors()) {
			return (ResponseEntity<List<?>>) utils.validar(result);
		}
		//new SolicitudResponse();
		
		listSolicitudResponse = solicitudService.consultaSolcitudesByFilter(filterRequest,"estados","VG");
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
	


	////////////////////////////////////////////////////////
	/////////////////////////////////////////////
	//////////////////////////////////////////REPORTES////////////
	////////////////////////////////////
	
	
	@PostMapping("/investor/reportes/porFecha")
    public ResponseEntity<?> consultaReportePorFecha( @RequestBody FilterIntSolicitudRequest filterRequest, BindingResult result) {
		ReportePorAnio reporte =null ;
		
		return ResponseEntity.ok(reporte);
	}
}
