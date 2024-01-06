package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Calificacion;
import com.multiplos.cuentas.repository.CalificacionRepository;
import com.multiplos.cuentas.services.CalificacionService;

@Service
public class CalificacionServiceImpl implements CalificacionService{

	private CalificacionRepository repository;
	
	@Autowired
	public CalificacionServiceImpl(CalificacionRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Calificacion> findAll() {
		return repository.findAll().stream().filter(c->c.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Calificacion findById(Long id) {
		Calificacion calificacion = null;
		try {
			Optional<Calificacion> calif = repository.findById(id);
			if(calif.get().getEstado().contains("A")) {
				calificacion = calif.get();
			}
		}catch (Exception e) {
            return calificacion;
        }
		return calificacion;
	}

}
