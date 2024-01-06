package com.multiplos.cuentas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.multiplos.cuentas.pojo.cuenta.EnviaEmailRequest;
import com.multiplos.cuentas.services.EnvioEmailService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class EnvioEmailController {
	
	private EnvioEmailService envioEmailService;
	
	@Autowired
	public EnvioEmailController(EnvioEmailService envioEmailService) {
		this.envioEmailService = envioEmailService;
	}

	@PostMapping(value="/emails/enviar")
	public ResponseEntity<String> enviaEmails(@RequestBody EnviaEmailRequest request) throws Exception{
		String mailMessage = null;
		mailMessage = envioEmailService.enviaEmails(request.getIdPlantilla());
		if(mailMessage.contains("ok")) {
			return ResponseEntity.ok("Emails enviados correctamente");
		}else {
			return ResponseEntity.badRequest().body(mailMessage);
		}
	}

}
