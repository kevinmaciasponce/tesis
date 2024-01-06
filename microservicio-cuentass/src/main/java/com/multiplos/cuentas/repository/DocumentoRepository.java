package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, Long>{
	
	@Query("select d from Documento d where d.documento.idTipoDocumento=?1 and d.estado='A'")
	Documento consultaDocumento(Long idTipoDocumento);

}
