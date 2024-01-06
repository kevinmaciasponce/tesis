package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.services.EmpresaService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class EmpresaController {
	
	private EmpresaService empresaService;

    @Autowired
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }
    
    @GetMapping(value = "/empresas")
    public ResponseEntity<List<Empresa>> consultaTblAmortizacion() {
    	List<Empresa> empresas = new ArrayList<>();
    	empresas = empresaService.consultaEmpresas();
    	if(empresas.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	}
        return ResponseEntity.ok(empresas);
    }
    

}
