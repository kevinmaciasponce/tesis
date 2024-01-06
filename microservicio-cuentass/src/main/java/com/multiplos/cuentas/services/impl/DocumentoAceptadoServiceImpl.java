package com.multiplos.cuentas.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Documento;
import com.multiplos.cuentas.models.DocumentoAceptado;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.pojo.documento.DocumentoAceptadoResponse;
import com.multiplos.cuentas.repository.DocumentoAceptadoRepository;
import com.multiplos.cuentas.services.DocumentoAceptadoService;
import com.multiplos.cuentas.services.DocumentoService;

@Service
public class DocumentoAceptadoServiceImpl implements DocumentoAceptadoService {

	private static final Logger LOG = LoggerFactory.getLogger(DocumentoAceptadoServiceImpl.class);
	private DocumentoAceptadoRepository repository;
	private DocumentoService documentoService;
	
	@Autowired
	public DocumentoAceptadoServiceImpl(DocumentoAceptadoRepository repository,DocumentoService documentoService) {
		this.repository = repository;
		this.documentoService = documentoService;
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentoAceptado findById(Long id) {
		DocumentoAceptado doc = null;
		try {
			DocumentoAceptado documento = repository.findByIdDocAceptado(id);
			if(documento.getEstado().contains("A")) {
				doc = documento;
			}
		}catch (Exception e) {
            return doc;
        }
		return doc;
	}

	@Override
	@Transactional
	public String guardaDocumentoGeneralAceptado(String identificacion, String usuario, String numeroSolicitud, int idTipoDocumento) {
		DocumentoAceptado documentoAcept = new DocumentoAceptado();
		try {
			
			documentoAcept = this.consultaDocumento((long) idTipoDocumento);
	    	if(documentoAcept != null) {
	    		documentoAcept.setHabilitado(false);
	    	}
	    	Documento doc = new Documento();
	    	doc = documentoService.consultaDocumento((long) idTipoDocumento);
	    	DocumentoAceptado docAceptado = new DocumentoAceptado();
	    	docAceptado.setNombre(doc.getNombre());
	    	docAceptado.setDocumento(doc.getDocumento());
	    	docAceptado.setFechaCreacion(LocalDateTime.now());
	    	docAceptado.setRuta(doc.getRuta());
	    	docAceptado.setVersion(doc.getVersion());
	    	docAceptado.setUsuarioCreacion(usuario);
	    	docAceptado.setNumeroSolicitud(numeroSolicitud);
	    	docAceptado.setHabilitado(true);
	    	Persona pers = new Persona();
	    	pers.setIdentificacion(identificacion);
	    	docAceptado.setPersona(pers);
			repository.save(docAceptado);
		}catch(Exception e) {
			LOG.error("Error No se pudo agregar el documento "+e.getMessage());
			return "No se pudo agregar el documento";
		}
		return "ok";
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentoAceptado consultaDocumento(Long idTipoDocumento) {
		return repository.consultaDocumento(idTipoDocumento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentoAceptadoResponse> consultaDocumentosAceptados(String identificacion, String numeroSolicitud) {
		List<DocumentoAceptado> listDocAceptados = new ArrayList<>();
		List<DocumentoAceptadoResponse> listDocResponse = new ArrayList<>();
		
		listDocAceptados = repository.consultaDocumentosAceptados(identificacion, numeroSolicitud);
		if(!listDocAceptados.isEmpty()) {
			listDocAceptados.forEach(da->{
				DocumentoAceptadoResponse doc = new DocumentoAceptadoResponse();
				doc.setNombre(da.getDocumento().getDocumento());
				doc.setFecha(da.getFechaCreacion());
				doc.setEstado(da.getEstado());
				doc.setVersion(da.getVersion());
				doc.setUrl(da.getRuta());
				
				listDocResponse.add(doc);
			});
		}
		return listDocResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentoAceptadoResponse> consultaDocumentosAceptadosHistorico(String identificacion,String numeroSolicitud) {
		List<DocumentoAceptado> listDocAceptadosHist = new ArrayList<>();
		List<DocumentoAceptadoResponse> listDocHistResponse = new ArrayList<>();
		
		listDocAceptadosHist = repository.consultaDocumentosAceptadosHistorico(identificacion, numeroSolicitud);
		if(!listDocAceptadosHist.isEmpty()) {
			listDocAceptadosHist.forEach(da->{
				//if(da.getVersion()-1=) { falta corregir
					DocumentoAceptadoResponse docHist = new DocumentoAceptadoResponse();
					docHist.setNombre(da.getDocumento().getDocumento());
					docHist.setFecha(da.getFechaCreacion());
					docHist.setEstado(da.getEstado());
					docHist.setVersion(da.getVersion());
					docHist.setUrl(da.getRuta());
					
					listDocHistResponse.add(docHist);
				//}
				
			});
		}
		return listDocHistResponse;
	}

	@Override
	@Transactional
	public String guardaDocumentoAceptado(DocumentoAceptado docAceptado) {
		try {
			repository.save(docAceptado);
		}catch(Exception e) {
			return "Error al agregar documento aceptado.";
		}
		return "ok";
	}

}
