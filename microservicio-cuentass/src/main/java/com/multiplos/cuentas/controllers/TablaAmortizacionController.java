package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

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

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorPrincipalResponse;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.UpdateSolicitudRequest;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class TablaAmortizacionController {
	
	private TablaAmortizacionService tblAmortService;
	private ServicesUtils utils;

    @Autowired
    public TablaAmortizacionController(TablaAmortizacionService tblAmortService, ServicesUtils utils) {
        this.tblAmortService = tblAmortService;
        this.utils = utils;
    }

  //  @PostMapping(value = "/loquesea2")
    @PostMapping(value = "/amortizacion/simulador")
    public ResponseEntity<?> simuladorTblAmortizacion(@Valid @RequestBody SimuladorRequest request, BindingResult result)throws Exception {
		AmortizacionResponse amortResponse = new AmortizacionResponse();
		List<SimuladorPrincipalResponse> simuladorPrincipal = new ArrayList<>();
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		try {
		if(request.getCodigoProyecto() != null) {
			amortResponse = tblAmortService.simuladorTblAmortizacion(request);
			if(amortResponse == null) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(amortResponse);
			
		}else {
			simuladorPrincipal = tblAmortService.simuladorPrincipal(request);
			if(simuladorPrincipal.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(simuladorPrincipal);
		}
		}catch(Exception e) {return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));}
		
    }
   //@PostMapping("/loquesea2")
   @PostMapping("/amortizacion/consulta")
    public ResponseEntity<?> consultaAmortizacion(@RequestParam(required=false) Long numSol,
    		@RequestParam(required=false) String codProyect,
    		@RequestParam Long tipo)throws Exception{
    	if((tipo==1 || tipo==2 )&& numSol== null) {return ResponseEntity.badRequest().body(new BadResponse( "Falta el numero de solicitud" ));}
    	if(tipo==3 && codProyect== null) {return ResponseEntity.badRequest().body(new BadResponse( "Falta el codigo del proyecto" ));}
    	AmortizacionResponse response= null;
    	try {
    		
    		if(tipo==1) {response =tblAmortService.consultaAmortizacionPorSolicitud(numSol);}
    		if(tipo==2) {response =tblAmortService.consultaPagarePorSolicitud(numSol);}
    		if(tipo==3) {response =tblAmortService.consultaAmortizacionPorProyecto(codProyect);}
    	
    	}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
    	return ResponseEntity.ok(response);
    }
    
    
    
//	@PostMapping(value = "/amortizacion/consulta")
//    public ResponseEntity<?> consultaTblAmortizacion(@Valid @RequestBody TipoTablaAmortizacionRequest request, BindingResult result) {
//		//AmortizacionResponse amortResponse = new AmortizacionResponse();
//    	TablaAmortizacion amortResponse = new TablaAmortizacion();
//		if(result.hasErrors()) {
//			return utils.validar(result);
//		}
//		if(request.getIdTipoTabla() == 3) {
//			if(request.getCodigoProyecto() != null) {
//				
//				
//				//amortResponse = tblAmortService.consultaTablaAmortizacion(request.getCodigoProyecto(), request.getIdTipoTabla());
//		
//			
//			}else {
//				return ResponseEntity.badRequest().body(new BadResponse("El campo codigoProyecto no debe ser nulo"));
//			}
//		}else if(request.getIdTipoTabla() == 1 || request.getIdTipoTabla() == 2 ) {
//			if(request.getNumeroSolicitud() != null) {
//				//amortResponse = tblAmortService.consultaTablaAmortizacion(request.getNumeroSolicitud(), request.getIdTipoTabla());
//				//amortResponse =tblAmortService.consultaAmortizacion(request.getNumeroSolicitud());
//			}else {
//				return ResponseEntity.badRequest().body(new BadResponse("El campo numeroSolicitud no debe ser nulo"));
//			}
//		}
//		
//		if(amortResponse == null) {
//			return ResponseEntity.badRequest().body(new BadResponse("No tiene generada una tabla de amortización"));
//		}
//		
//        return ResponseEntity.ok(amortResponse);
//    }

	

	
	
	@PutMapping(value = "/amortizacion")
    public ResponseEntity<?> actualizaAmortizacion(@Valid @RequestBody UpdateSolicitudRequest request, BindingResult result) {
		AmortizacionResponse amortResponse = new AmortizacionResponse();
			    
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		try {
			amortResponse = tblAmortService.updateTblAmortizacion(request);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
        return ResponseEntity.ok(amortResponse);
    }
	
}
