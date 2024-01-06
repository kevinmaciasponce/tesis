package com.multiplos.cuentas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.FechaGeneraTblAmortizacion;

public interface FechaGeneraTblAmortizacionRepository extends JpaRepository<FechaGeneraTblAmortizacion, Long> {

	@Query("select f from FechaGeneraTblAmortizacion f where f.proyecto.idProyecto = ?1")
	FechaGeneraTblAmortizacion consultaFechaGenTblPorProyecto(String idProyecto);
	
	@Query("select f.proyecto.idProyecto from FechaGeneraTblAmortizacion f where f.fechaGeneracion = ?1 ")
	List<String> consultaProyectoFechaGenTblPorFecha(LocalDate fechaGeneracion);
	
	@Query("select f from FechaGeneraTblAmortizacion f where f.fechaCreacion = ?1")
	List<FechaGeneraTblAmortizacion> consultaFechaGenTblPorFechaCreacion(LocalDate fecha);
	
}
