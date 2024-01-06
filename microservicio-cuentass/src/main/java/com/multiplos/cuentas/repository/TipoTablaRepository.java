package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.TipoTabla;

public interface TipoTablaRepository extends JpaRepository<TipoTabla, Long> {

	@Query("select te from TipoTabla te where te.idTipoTabla=?1 and te.estado='A'")
	TipoTabla consultaTipoTabla(Long id);
}
