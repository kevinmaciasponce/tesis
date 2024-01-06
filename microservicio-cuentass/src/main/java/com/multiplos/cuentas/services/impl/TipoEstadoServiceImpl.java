package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.repository.TipoEstadoRepository;
import com.multiplos.cuentas.services.TipoEstadoService;

@Service
public class TipoEstadoServiceImpl implements TipoEstadoService {

	private TipoEstadoRepository repository;
	
	@Autowired
	public TipoEstadoServiceImpl(TipoEstadoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoEstado> findAll() {
		return repository.findAll().stream().filter(c->c.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public TipoEstado findById(String id) {
		return repository.consultaTipoEstado(id.toUpperCase());
	}

}
