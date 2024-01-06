package com.multiplos.cuentas.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Token;
import com.multiplos.cuentas.pojo.cuenta.UsuarioRequest;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.TokenService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class EmailActivacionController {
	
	private CuentaService cuentaService;
	private TokenService tokenService;
	private ServicesUtils serviceUtils;
	private ParametroService parametroService;
	private EnvioEmailService envioEmailService;
	
	@Autowired
	public EmailActivacionController(CuentaService cuentaService,TokenService tokenService,ServicesUtils serviceUtils, ParametroService parametroService,
			EnvioEmailService envioEmailService) {
		this.cuentaService = cuentaService;
		this.tokenService = tokenService;
		this.serviceUtils = serviceUtils;
		this.parametroService = parametroService;
		this.envioEmailService = envioEmailService;
	}
		
	@PostMapping(value="/validacion-email")
	public ResponseEntity<?> enviaMailValidacion(@Valid @RequestBody UsuarioRequest user, BindingResult result) throws Exception{
		String message = null;
		Cuenta cuenta = new Cuenta();
		Map<String, Object> mensaje = new HashMap<>();
		ParametroResponse paramUrl = null;
		String urlToken;
		
		if(result.hasErrors()) {
			return serviceUtils.validar(result);
		}
		
		cuenta = cuentaService.findByCuenta(user.getEmail());
		if(cuenta == null) {
			message = "No existe cuenta";
			mensaje.put("mensaje", message);
		}else if(cuenta.getCuentaActiva().contains("S")) {
			message = "El usuario ya tiene la cuenta activada.";
			mensaje.put("mensaje", message);
		}else {
			Token confirmationToken = new Token();
			Date fechaActual = new Date();
			confirmationToken.setToken(UUID.randomUUID().toString());
			confirmationToken.setCuenta(cuenta);
			confirmationToken.setFechaCreacion(fechaActual);
						
			String generoToken = tokenService.generaToken(confirmationToken);
			if(generoToken.contains("ok")) {
				paramUrl = new ParametroResponse();
				paramUrl = parametroService.findByParametroCodParametro("URL_ACTIVACTA");
				
				//urlToken = paramUrl.getValor().concat("=").concat(confirmationToken.getToken());
				urlToken = paramUrl.getValor().concat(confirmationToken.getToken());
				
				message = envioEmailService.enviaEmailInvestor("ACTIVA_CUENTA", urlToken, cuenta.getEmail());
				mensaje.put("mensaje", message);
				
			}else {
				message = generoToken;
				mensaje.put("mensaje", message);
			}			
		}
		
		return ResponseEntity.ok(mensaje);
	}
	
	@PutMapping(value="/confirmacion-email/{paramToken}")
	public ResponseEntity<?> confirmaCuenta(@PathVariable String paramToken){
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		Token token = tokenService.findByToken(paramToken);
		
		if(token != null){
			response = cuentaService.confirmarCuenta(token.getCuenta().getIdCuenta());
		}else{
			response = "El enlace no es válido";
		}
		mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
	}
	
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
	{
		String response;
		//Cuenta cuenta = new Cuenta(); 
		Token token = tokenService.findByToken(confirmationToken);
		if(token != null){
			response = cuentaService.confirmarCuenta(token.getCuenta().getIdCuenta());
			
			modelAndView.addObject("message",response);
			modelAndView.setViewName("accountVerified");
			
		}else{
			modelAndView.addObject("message","El enlace no es válido.");
			modelAndView.setViewName("accountVerified");
		}
				
		return modelAndView;
	}
	
}
