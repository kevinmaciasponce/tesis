package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.DocumentoAceptado;

public interface DocumentoAceptadoRepository extends JpaRepository<DocumentoAceptado, Long> {

	DocumentoAceptado findByIdDocAceptado(Long id);
	
	@Query("select d from DocumentoAceptado d where d.documento.idTipoDocumento=?1 and d.habilitado=true")
	DocumentoAceptado consultaDocumento(Long idTipoDocumento);
	
	@Query("select d from DocumentoAceptado d where (:identificacion is null or d.persona.identificacion = :identificacion) "
			+ " and (:numeroSolicitud is null or d.numeroSolicitud = :numeroSolicitud) and d.habilitado=true")
	List<DocumentoAceptado> consultaDocumentosAceptados(String identificacion, String numeroSolicitud);
	
	@Query("select d from DocumentoAceptado d where (:identificacion is null or d.persona.identificacion = :identificacion) "
			+ " and (:numeroSolicitud is null or d.numeroSolicitud = :numeroSolicitud) and d.habilitado=false")
	List<DocumentoAceptado> consultaDocumentosAceptadosHistorico(String identificacion, String numeroSolicitud);
	
}
