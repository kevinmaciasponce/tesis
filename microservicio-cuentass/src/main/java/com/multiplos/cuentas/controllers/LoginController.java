package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Menu;
import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.pojo.login.RequestLogin;
import com.multiplos.cuentas.pojo.login.ResponseLogin;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.MenuService;
import com.multiplos.cuentas.services.RolService;
import com.multiplos.cuentas.services.impl.CuentaServiceImpl;
import com.multiplos.cuentas.utils.ResponseError;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class LoginController {
	
	private CuentaService cuentaService;
	private ServicesUtils utils;
	private MenuService menuService;
	private RolService rolService;
	private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);

    @Autowired
    public LoginController(CuentaService cuentaService, ServicesUtils utils,RolService rolService,MenuService menuService) {
        this.cuentaService = cuentaService;
        this.utils = utils;
        this.rolService = rolService;
        this.menuService = menuService;
    }

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@Valid @RequestBody RequestLogin login, BindingResult result){
		LOG.warn("entra al try");
		ResponseLogin response = new ResponseLogin();
		ResponseError responseError = null;
		if(result.hasErrors()) {
			return (ResponseEntity<ResponseLogin>) utils.validar(result);
		}
		//response = cuentaService.validaLogin(login);
		
//		if(response.getMensaje() != "ok") {
//			responseError = new ResponseError();
//			responseError.setError(response.getMensaje());
//			return ResponseEntity.badRequest().body(responseError);
//		}
		
		return ResponseEntity.ok(response);
	}
	
	//solo pruebas
	@GetMapping(value = "/rol/{rol}")
    /*public ResponseEntity<List<Rol>> obtieneRoles(){
		List<Rol> rol = new ArrayList<>(); 
		rol = rolService.findAll();
		if(rol.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(rol);
    }*/
	
	public ResponseEntity<Rol> obtieneRoles(@PathVariable String rol){
		Rol rolg = new Rol(); 
		rolg = rolService.consultaRol(rol);
		if(rolg == null) {
			ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(rolg);
    }
	
	//solo pruebas
	@GetMapping(value = "/menu")
    public ResponseEntity<List<Menu>> obtieneMenu(){
		List<Menu> menu = new ArrayList<>(); 
		menu = menuService.findAll();
		if(menu.isEmpty()) {
			ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(menu);
    }
    
}
