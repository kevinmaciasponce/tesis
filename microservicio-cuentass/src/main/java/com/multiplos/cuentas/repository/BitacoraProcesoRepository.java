package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.BitacoraProceso;

public interface BitacoraProcesoRepository extends JpaRepository<BitacoraProceso, Long> {
	
	@Query("select b from BitacoraProceso b where b.proceso = ?1 and b.tipo = ?2 ")
	BitacoraProceso consultaBitacoraPorProcesoAndTipo(String proceso, String tipo);

}
