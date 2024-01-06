package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.TipoDocumento;

public interface TipoDocumentoService {
	
	TipoDocumento findByIdTipoDocumento(Long id);
	List<TipoDocumento> findAllTipoDocumento();

}
