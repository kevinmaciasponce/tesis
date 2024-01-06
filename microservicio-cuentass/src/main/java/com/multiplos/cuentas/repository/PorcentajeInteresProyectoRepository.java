package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.PorcentajeInteresProyecto;

public interface PorcentajeInteresProyectoRepository extends JpaRepository<PorcentajeInteresProyecto, Long> {

	@Query("select pi from PorcentajeInteresProyecto pi where pi.id=?1 and pi.estado='A'")
	PorcentajeInteresProyecto consultaPorcentajeInteresPorId(Long id);
	
	@Query("select pi from PorcentajeInteresProyecto pi where pi.proyecto.idProyecto=?1 and pi.estado='A'")
	List<PorcentajeInteresProyecto> consultaPorcentajeInteresPorProyecto(String codigoProyecto);
	
}
