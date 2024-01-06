package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.PorcentajeInteresProyecto;
import com.multiplos.cuentas.repository.PorcentajeInteresProyectoRepository;
import com.multiplos.cuentas.services.PorcentajeInteresProyectoService;

@Service
public class PorcentajeInteresProyectoServiceImpl implements PorcentajeInteresProyectoService {

	private PorcentajeInteresProyectoRepository repository;
	
	@Autowired
	public PorcentajeInteresProyectoServiceImpl(PorcentajeInteresProyectoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PorcentajeInteresProyecto> findAll() {
		return repository.findAll().stream().filter(c->c.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public PorcentajeInteresProyecto findById(Long id) {
		return repository.consultaPorcentajeInteresPorId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PorcentajeInteresProyecto> consultaPorcentajesPorProyecto(String codigoProyecto) {
		return repository.consultaPorcentajeInteresPorProyecto(codigoProyecto);
	}

}
