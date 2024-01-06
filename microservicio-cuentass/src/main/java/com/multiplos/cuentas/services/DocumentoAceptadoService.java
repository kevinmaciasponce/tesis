package com.multiplos.cuentas.services;


import java.util.List;

import com.multiplos.cuentas.models.DocumentoAceptado;
import com.multiplos.cuentas.pojo.documento.DocumentoAceptadoResponse;

public interface DocumentoAceptadoService {

	DocumentoAceptado findById(Long id);
	String guardaDocumentoGeneralAceptado(String identificacion, String usuario, String numeroSolicitud, int idTipoDocumento);
	String guardaDocumentoAceptado(DocumentoAceptado docAceptado);
	DocumentoAceptado consultaDocumento(Long idTipoDocumento);
	List<DocumentoAceptadoResponse> consultaDocumentosAceptados(String identificacion, String numeroSolicitud);
	List<DocumentoAceptadoResponse> consultaDocumentosAceptadosHistorico(String identificacion, String numeroSolicitud);
}
