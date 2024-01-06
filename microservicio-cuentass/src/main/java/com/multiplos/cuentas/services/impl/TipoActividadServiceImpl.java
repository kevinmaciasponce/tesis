package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.TipoActividad;
import com.multiplos.cuentas.repository.TipoActividadRepository;
import com.multiplos.cuentas.services.TipoActividadService;

@Service
public class TipoActividadServiceImpl implements TipoActividadService {

	private TipoActividadRepository repository;
	
	@Autowired
	public TipoActividadServiceImpl(TipoActividadRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipoActividad> findAll() {
		return repository.findAll().stream().filter(c->c.getEstado().contains("A")).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public TipoActividad findById(Long id) {
		TipoActividad actividad = null;
		try {
			Optional<TipoActividad> tipoActividad = repository.findById(id);
			if(tipoActividad.get().getEstado().contains("A")) {
				actividad = tipoActividad.get();
			}
		}catch (Exception e) {
            return actividad;
        }
		return actividad;
	}

}
