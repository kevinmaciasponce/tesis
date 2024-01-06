package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Banco;
import com.multiplos.cuentas.services.BancoService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class BancoController {

	private BancoService bancoService;
	private ServicesUtils utilService;

    @Autowired
    public BancoController(BancoService bancoService,ServicesUtils utilService) {
        this.bancoService = bancoService;
        this.utilService = utilService;
    }
    
    @GetMapping("/bancos")
    public ResponseEntity<List<Banco>> obtieneBancos(){
		List<Banco> banco = new ArrayList<>(); 
		banco = bancoService.findAll("A");
		if(banco.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(banco);
    }
    
    @GetMapping("/bancos/{id}")
    public ResponseEntity<Banco> obtieneBanco(@PathVariable Long id){
    	Banco banco = new Banco();
    	banco = bancoService.findById(id);
		if(banco == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(banco);
    }
    
    @PostMapping("/bancos")
    public ResponseEntity<?> creaBanco(@Valid @RequestBody Banco banco, BindingResult result)throws Exception{
    	Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return utilService.validar(result);
		}
		String ciudadResponse = bancoService.save(banco);
		mensaje.put("mensaje", ciudadResponse);
		return ResponseEntity.ok(mensaje);
    }
    
    @DeleteMapping("/bancos/{id}")
    public ResponseEntity<?> eliminaBanco(@PathVariable Long id){
    	Map<String, Object> mensaje = new HashMap<>();
    	String response = bancoService.deleteBanco(id);
    	mensaje.put("mensaje", response);
		return ResponseEntity.ok(mensaje);
    }
}
