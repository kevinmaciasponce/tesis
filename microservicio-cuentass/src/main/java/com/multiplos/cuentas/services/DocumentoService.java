package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Documento;

public interface DocumentoService {

	List<Documento> findAll();
	Documento findById(Long id);
	String guardaDocumento(Documento documento);
	String eliminaDocumento(Long id);
	Documento consultaDocumento(Long idTipoDocumento);
}
