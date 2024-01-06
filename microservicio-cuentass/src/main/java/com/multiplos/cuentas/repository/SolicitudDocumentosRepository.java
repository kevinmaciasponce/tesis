package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.SolicitudDocumentos;



public interface SolicitudDocumentosRepository  extends JpaRepository<SolicitudDocumentos, Long> {

	@Query("select s from SolicitudDocumentos s where s.solicitud.numeroSolicitud = :numeroSolicitud and s.estado is 'A'")
	SolicitudDocumentos consultaSolicitud(Long numeroSolicitud);

	@Query(value = "select s from SolicitudDocumentos s where s.estado is 'A'")
	List<SolicitudDocumentos> consultarTodos();
}
