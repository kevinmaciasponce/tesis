package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.pojo.documento.DocumentoAceptadoResponse;
import com.multiplos.cuentas.pojo.documento.DocumentoRequest;
import com.multiplos.cuentas.services.DocumentoAceptadoService;

@RestController
@RequestMapping("${route.service.contextPath}")
public class DocumentoController {

	private DocumentoAceptadoService docAceptadoService;
	
	@Autowired
	public DocumentoController(DocumentoAceptadoService docAceptadoService) {
		this.docAceptadoService = docAceptadoService;
	}
	
	@PostMapping(value = "/documentos/aceptados")
    public ResponseEntity<List<DocumentoAceptadoResponse>> consultaDocumentosAceptados(@RequestBody DocumentoRequest request) {
		List<DocumentoAceptadoResponse> persona = new ArrayList<>();
		persona = docAceptadoService.consultaDocumentosAceptados(request.getIdentificacion(),request.getNumeroSolicitud());
		if(persona.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(persona);
    }
	
	@PostMapping(value = "/documentos/aceptados/historico")
    public ResponseEntity<List<DocumentoAceptadoResponse>> consultaDocumentosAceptadosHistoricos(@RequestBody DocumentoRequest request) {
		List<DocumentoAceptadoResponse> persona = new ArrayList<>();
		//falta ajustar 
		persona = docAceptadoService.consultaDocumentosAceptadosHistorico(request.getIdentificacion(),request.getNumeroSolicitud());
		if(persona.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(persona);
    }

	
}
