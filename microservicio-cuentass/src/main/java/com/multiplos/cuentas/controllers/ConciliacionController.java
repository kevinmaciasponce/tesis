package com.multiplos.cuentas.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.ConciliacionXls;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ConciliacionController {



	private ConciliacionesService csvService;
	
	@Autowired
	public ConciliacionController( ServicesUtils utilService,ConciliacionesService  csvService) {
		
		this.csvService= csvService;
		
	}
	

	
}
