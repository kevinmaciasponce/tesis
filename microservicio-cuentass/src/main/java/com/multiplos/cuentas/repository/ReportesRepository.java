package com.multiplos.cuentas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.reportes.ReporteCuotas;
import com.multiplos.cuentas.reportes.ReportePorProyecto;
import com.multiplos.cuentas.reportes.reportesMora.ReporteCuotasPorPagos;

public interface ReportesRepository extends JpaRepository<Solicitud, Long> {

	
	@Query("select new com.multiplos.cuentas.reportes.ReportePorProyecto(s.proyecto.idProyecto, CONCAT(s.proyecto.empresa.nombre,' R',s.proyecto.ronda)  ) "
			+ " from DetalleAmortizacion d, Solicitud s "
			+ " where  (:mes is null or EXTRACT(MONTH FROM d.fechaEstimacion) =:mes) and  EXTRACT(YEAR FROM d.fechaEstimacion)=:anio"
			+ "  and s.amortizacion.idTblAmortizacion = d.tablaAmortizacion.idTblAmortizacion"
			+ " and s.inversionista.persona.identificacion=:identificacion "
			+ " and d.detalleCobro != 'Totales'"
			+ " GROUP BY s.proyecto.idProyecto, s.proyecto.empresa.nombre,s.proyecto.ronda,d.fechaEstimacion")
	List<ReportePorProyecto> reporteProyectoPorMes( int mes,int anio, String identificacion);
	
	@Query("select new com.multiplos.cuentas.reportes.ReporteCuotas(s.proyecto.idProyecto, CONCAT(s.proyecto.empresa.nombre,' R',s.proyecto.ronda),EXTRACT(MONTH FROM d.fechaEstimacion), d.fechaEstimacion,CONCAT(d.cuota,'/',s.amortizacion.plazo ), d.totalRecibir,d.estadoPago  ) "
			+ " from DetalleAmortizacion d, Solicitud s "
			+ " where EXTRACT(MONTH FROM d.fechaEstimacion) =:mes and  EXTRACT(YEAR FROM d.fechaEstimacion)=:anio"
			+ "  and s.amortizacion.idTblAmortizacion = d.tablaAmortizacion.idTblAmortizacion"
			+ " and s.inversionista.persona.identificacion=:identificacion "
			+ " and d.detalleCobro != 'Totales'"
			+ " order by :mes")
	List<ReporteCuotas> generearReportePorFechaYProyecto( int mes,int anio, String identificacion);
	
	@Query("select new com.multiplos.cuentas.reportes.ReporteCuotas( s.proyecto.idProyecto, CONCAT(s.proyecto.empresa.nombre,' R',s.proyecto.ronda),EXTRACT(MONTH FROM d.fechaEstimacion), d.fechaEstimacion,CONCAT(d.cuota,'/',s.amortizacion.plazo ), d.totalRecibir,d.estadoPago  ) "
			+ " from DetalleAmortizacion d, Solicitud s "
			+ " where  EXTRACT(MONTH FROM d.fechaEstimacion) =:mes and  EXTRACT(YEAR FROM d.fechaEstimacion)=:anio"
			+ "  and s.amortizacion.idTblAmortizacion = d.tablaAmortizacion.idTblAmortizacion"
			+ " and s.inversionista.persona.identificacion=:identificacion "
			+ " and s.proyecto.idProyecto=:codProyect "
			+ " and d.detalleCobro != 'Totales'"
			
			+ " order by :mes")
	List<ReporteCuotas> generearReportePorFechaYProyecto2( int mes,int anio, String identificacion,String codProyect);
	
	
	
	@Query("select new com.multiplos.cuentas.reportes.reportesMora.ReporteCuotasPorPagos( d.fechaEstimacion, CONCAT(d.cuota,'/',s.amortizacion.plazo)) "
			+ " from DetalleAmortizacion d, Solicitud s "
			+ " where  EXTRACT(MONTH FROM d.fechaEstimacion) =:mes and  EXTRACT(YEAR FROM d.fechaEstimacion)=:anio"
			+ " and s.amortizacion.idTblAmortizacion = d.tablaAmortizacion.idTblAmortizacion"
			+ " and s.proyecto.idProyecto=:codProyect"
			+ " and d.fechaRealizada is not null"
			+ " group by d.fechaEstimacion, d.cuota,s.amortizacion.plazo ")
	ReporteCuotasPorPagos generarReporteCuotasPorPagos(int mes, int anio,String codProyect);
	
	@Query("select SUM( (EXTRACT(epoch from age(d.fechaRealizada, d.fechaEstimacion)) / 86400))"
			+ " from DetalleAmortizacion d, Solicitud s "
			+ " where  EXTRACT(MONTH FROM d.fechaEstimacion) =:mes and  EXTRACT(YEAR FROM d.fechaEstimacion)=:anio"
			+ " and s.amortizacion.idTblAmortizacion = d.tablaAmortizacion.idTblAmortizacion"
			+ " and s.proyecto.idProyecto=:codProyect"
			+ " and d.fechaRealizada is not null"
		)
	int diasAtrasoPorMes(int mes, int anio,String codProyect);
	
}
