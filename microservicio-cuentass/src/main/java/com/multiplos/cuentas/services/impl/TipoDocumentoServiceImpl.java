package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.TipoDocumento;
import com.multiplos.cuentas.repository.TipoDocumentoRepository;
import com.multiplos.cuentas.services.TipoDocumentoService;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

	private TipoDocumentoRepository repository;
	
	@Autowired
	public TipoDocumentoServiceImpl(TipoDocumentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public TipoDocumento findByIdTipoDocumento(Long id) {
		return repository.findByIdTipoDocumento(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoDocumento> findAllTipoDocumento() {
		List<TipoDocumento> tipoDoc = new ArrayList<>();
		tipoDoc = repository.findAll().stream().filter(t->t.getEstado().contains("A")).collect(Collectors.toList());
		return tipoDoc;
	}

}
