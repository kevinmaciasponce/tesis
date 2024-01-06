package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.TipoEstado;

public interface TipoEstadoRepository extends JpaRepository<TipoEstado, Long> {
	
	@Query("select te from TipoEstado te where te.idEstado=?1 and te.estado='A'")
	TipoEstado consultaTipoEstado(String idEstado);
}
