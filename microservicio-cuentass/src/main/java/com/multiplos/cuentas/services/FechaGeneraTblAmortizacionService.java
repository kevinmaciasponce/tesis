package com.multiplos.cuentas.services;

import java.time.LocalDate;
import java.util.List;

import com.multiplos.cuentas.models.FechaGeneraTblAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.FechaGenTblAmortRequest;

public interface FechaGeneraTblAmortizacionService {

	Boolean validarProyecto(String codProyect)throws Exception;
	
	String guardaFechaGeneracionAmortizacion(FechaGenTblAmortRequest fechaGenTblAmortizacion) throws Exception;
	FechaGeneraTblAmortizacion consultaFechaGenTblPorProyecto(String idProyecto);
	List<String> consultaProyectoFechaGenTblPorFecha(LocalDate fechaGeneracion);
	List<FechaGeneraTblAmortizacion> consultaFechaGenTblPorFechaCreacion(LocalDate fecha);
	
	String procesoGeneraTablaAmortizacionMasiva(String tipo)throws Exception;
	String actualizaFechaGeneracionAmortizacion(FechaGenTblAmortRequest fechaGenTblAmortizacion);
}
