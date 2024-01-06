package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Documento;
import com.multiplos.cuentas.repository.DocumentoRepository;
import com.multiplos.cuentas.services.DocumentoService;

@Service
public class DocumentoServiceImpl implements DocumentoService {

	private DocumentoRepository repository;
	
	@Autowired
	public DocumentoServiceImpl(DocumentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Documento> findAll() {
		return repository.findAll().stream().filter(d->d.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Documento findById(Long id) {
		Documento doc = null;
		try {
			Optional<Documento> documento = repository.findById(id);
			if(documento.get().getEstado().contains("A")) {
				doc = documento.get();
			}
		}catch (Exception e) {
            return doc;
        }
		return doc;
	}

	@Override
	@Transactional
	public String guardaDocumento(Documento documento) {
		try {
			repository.save(documento);
		}catch(Exception e) {
			return "No se pudo agregar el documento";
		}		
		return "ok";
	}

	@Override
	@Transactional
	public String eliminaDocumento(Long id) {
		Documento doc = this.findById(id);
		if(doc != null) {
			doc.setEstado("I");
			repository.save(doc);
			return "Documento eliminado con exito";			
		}else {
			return "No se pudo eliminar, documento no existe";
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Documento consultaDocumento(Long idTipoDocumento) {
		return repository.consultaDocumento(idTipoDocumento);
	}

	
}
