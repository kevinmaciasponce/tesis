package com.multiplos.cuentas.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.services.ParametroService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ParametroController {
	
	ParametroService parametroService;

    @Autowired
    public ParametroController(ParametroService parametroService) {
        this.parametroService = parametroService;
    }
    
	@GetMapping(value = "/parametros/{codParam}")
    public ResponseEntity<List<ParametroResponse>> obtieneParametro(@PathVariable String codParam){
		List<ParametroResponse> parametro = parametroService.findByParametro(codParam);
    	return ResponseEntity.ok(parametro);
    }

}
