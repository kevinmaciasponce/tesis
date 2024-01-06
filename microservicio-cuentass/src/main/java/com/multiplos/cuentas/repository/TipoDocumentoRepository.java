package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.TipoDocumento;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
	
	TipoDocumento findByIdTipoDocumento(Long id);
}
