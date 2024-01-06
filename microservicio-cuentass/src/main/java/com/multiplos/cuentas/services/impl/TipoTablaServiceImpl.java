package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.TipoTabla;
import com.multiplos.cuentas.repository.TipoTablaRepository;
import com.multiplos.cuentas.services.TipoTablaService;

@Service
public class TipoTablaServiceImpl implements TipoTablaService {

	private TipoTablaRepository repository;
	
	@Autowired
	public TipoTablaServiceImpl(TipoTablaRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoTabla> findAll() {
		return repository.findAll().stream().filter(c->c.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public TipoTabla findById(Long id) {
		return repository.consultaTipoTabla(id);
	}

}
