package com.multiplos.cuentas.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.pojo.cuenta.UsuarioRequest;
import com.multiplos.cuentas.pojo.formulario.FormNatRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocumentoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.FormContact;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.pojo.cuenta.CuentaRequest;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.impl.CuentaServiceImpl;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class CuentaController {
	private static final Logger LOG = LoggerFactory.getLogger(CuentaController.class);
	private CuentaService cuentaService;
	private ServicesUtils utils;
	private SolicitudService solicitudService;
    @Autowired
    public CuentaController(CuentaService cuentaService,ServicesUtils utils,SolicitudService solicitudService) {
        this.cuentaService = cuentaService;
        this.utils = utils;
        this.solicitudService= solicitudService;
    }
    
    @PostMapping("/representante/eliminar/cuenta/filter")
	public  ResponseEntity<?> eliminarBene()throws Exception{
		Object bene=null;
		
		try {
			 this.cuentaService.eliminarCuenta();
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( bene);
	}
    
	@PostMapping(value = "/cuentas")
    public ResponseEntity<?> creaCuenta(@Valid @RequestBody CuentaRequest cuentaRequest, BindingResult result) {
		String cuentaResponse = null;
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		try {
		cuentaResponse = cuentaService.save(cuentaRequest);
		}catch(Exception e ){
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}		
        return ResponseEntity.ok(new Response(cuentaResponse));
    }
	
	
	//@PostMapping(value = "/loquesea2")
	@PostMapping(value = "/cuentas/beneficiario")
    public ResponseEntity<?> creaCuentaBeneficiario(@Valid @RequestBody CuentaRequest cuentaRequest, BindingResult result) {
		FilterSolicitudRequest mapper= new FilterSolicitudRequest() ;
		Map<String, Object> mensaje = new HashMap<>();
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		String cuentaResponse = null;
	
//		if(result.hasErrors()) {
//			return utils.validar(result);
//		}
		//cuentaResponse = cuentaService.saveCuenta(cuentaRequest,persona);
//		if(cuentaResponse == "500" || (cuentaResponse != "ok" && cuentaResponse != "Es promotor" && cuentaResponse != "Se envió un correo a su Email" )) {
//			mensaje.put("error", cuentaResponse);
//			return ResponseEntity.badRequest().body(mensaje);
//		}
		mensaje.put("mensaje", cuentaRequest);
        return ResponseEntity.ok(mensaje);
    }
	
	
	
	
	@PostMapping(value = "/cuentas/validaCuenta")
    public ResponseEntity<?> validaEmail(@Valid @RequestBody UsuarioRequest cuentaEmail, BindingResult result){
		boolean existe = false;
		Map<String, Object> mensaje = new HashMap<>();
		
		if(result.hasErrors()) {
			return utils.validar(result);
		}
		
		existe = cuentaService.verificaCuenta(cuentaEmail.getEmail());
		if(existe) {
			mensaje.put("mensaje", "Ya existe una cuenta con el email ingresado.");
			return ResponseEntity.badRequest().body(mensaje);
		}else {
			mensaje.put("mensaje", "ok");
			return ResponseEntity.ok(mensaje);
		}
    }
	
	//@PostMapping("loquesea2")
		@PostMapping("/solicitud/formularios/nat")
	    public ResponseEntity<?> creaFormularioNat(@Valid @RequestBody FormNatRequest request, BindingResult result) {
			String formResponse = null;
			Map<String, Object> mensaje = new HashMap<>();
		    
			if(result.hasErrors()) {
				return utils.validar(result);
			}
			try {
			formResponse = solicitudService.guardaPersonaInfoAdicional(request,null,null);
			}catch (Exception e) {
	    		return ResponseEntity.badRequest().body(new BadResponse(formResponse,e.getMessage()));	
	    	}
			if(formResponse != "ok") {
				mensaje.put("error", formResponse);
				return ResponseEntity.badRequest().body(mensaje);
			}
			mensaje.put("mensaje", "Guardado exitosamente.");
			
	        return ResponseEntity.ok(mensaje);
	    }
	
	 @PostMapping("confirmacion-email/forgot-pass")
	    public ResponseEntity<?> forgotPass(@Valid @RequestParam String mail, @RequestParam String identificacion) throws Exception{
		 String formResponse;
		 try {
			 formResponse= cuentaService.forgotPass(mail, identificacion);
		 }catch(Exception e){
			 
			 return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		 }
		 return ResponseEntity.ok().body(new Response(formResponse));
	    }
	 
	 @PostMapping("confirmacion-email/valid-switch-pass")
	    public ResponseEntity<?> validSwitchPass(@Valid @RequestParam String token) throws Exception{
		 String formResponse="false";
		 boolean valido;
		 try {
			  valido = cuentaService.validSwitchPass(token);
		 }catch(Exception e){
			 return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		 }
		 if(valido) {
			 formResponse = "true";
		 } 
		 return ResponseEntity.ok().body(new Response(formResponse));
	    }
	 
	 @PostMapping("confirmacion-email/switch-pass")
	    public ResponseEntity<?> switchPass(@Valid @RequestParam String token, @RequestParam String pass) throws Exception{
		 String formResponse;
		 try {
			 formResponse= cuentaService.switchPass(token, pass);
		 }catch(Exception e){
			 
			 return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		 }
		 return ResponseEntity.ok().body(new Response(formResponse));
	    }
	 
	 
	 @PostMapping("/public/cuenta/generated")
	    public ResponseEntity<?> AsignarNuevoRoll(@Valid @RequestParam String token, @RequestParam String pass) throws Exception{
		 String formResponse;
		 try {
			 formResponse= cuentaService.switchPass(token, pass);
		 }catch(Exception e){
			 
			 return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		 }
		 return ResponseEntity.ok().body(new Response(formResponse));
	    }
	 
	 
	 
	 
	 
	 
	
	/*//solo para pruebas
	@GetMapping(value = "/cuentas/{identificacion}/{pass}")
	public ResponseEntity<?> verificaClave(@PathVariable String identificacion, @PathVariable String pass){
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		response = cuentaService.validaClave(identificacion,pass);
		mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
	}
	
	//solo para pruebas
	@GetMapping(value = "/cuentas/{pass}")
	public ResponseEntity<?> encriptaClave(@PathVariable String pass){
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		response = cuentaService.encriptaClave(pass);
		mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
	}*/
	
}
