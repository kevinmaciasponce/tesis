package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.TipoEstado;

public interface TipoEstadoService {

	List<TipoEstado> findAll();
	TipoEstado findById(String id);
}
