package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.PorcentajeSolicitudAprobada;

public interface PorcentajeSolicitudAprobadaRepository extends JpaRepository<PorcentajeSolicitudAprobada, Long>{
	
	@Query("select p from PorcentajeSolicitudAprobada p where p.proyecto.idProyecto = ?1 and p.estado='A'")
	PorcentajeSolicitudAprobada consultaPorcSolAprobado(String codigoProyecto);

}
