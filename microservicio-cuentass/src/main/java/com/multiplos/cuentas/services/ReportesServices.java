package com.multiplos.cuentas.services;

import java.time.LocalDate;
import java.util.List;

import com.multiplos.cuentas.reportes.ReporteGeneral;
import com.multiplos.cuentas.reportes.ReportePorAnio;
import com.multiplos.cuentas.reportes.ReportePorProyecto;
import com.multiplos.cuentas.reportes.fechaValue;


public interface ReportesServices {
	
	ReporteGeneral reportePorfechasInvestor( List<fechaValue> fechas,List<ReportePorProyecto> listaProyecto , String identificacion)throws Exception ;
	ReportePorAnio GerenteReportePorfechasInvestor( int[] meses,int anio,String[] identificacion)throws Exception ;
	ReportePorAnio reportePorProyectosMora(int[] meses,int anio,String[] codProyect)throws Exception;
	
}
