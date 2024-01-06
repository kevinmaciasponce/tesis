package com.multiplos.cuentas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionDetalleResponse;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse;

import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;

public interface TablaAmortizacionRepository extends JpaRepository<TablaAmortizacion, Long> {
		
	
	@Query("select new com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse(s.amortizacion.idTblAmortizacion,CONCAT(s.inversionista.persona.nombres,' ',s.inversionista.persona.apellidos),s.proyecto.empresa.pais.pais,s.proyecto.empresa.nombre,s.proyecto.idProyecto,s.amortizacion.montoInversion,"
			+ " s.amortizacion.fechaCreacion, s.amortizacion.plazo, s.amortizacion.rendimientoNeto, s.amortizacion.rendimientoTotalInversion,s.amortizacion.totalRecibir,s.amortizacion.fechaEfectiva) "
			+ " from Solicitud s where s.numeroSolicitud=:numeroSolicitud ")
	AmortizacionResponse amortizacionPorSolicitud (Long numeroSolicitud);
	
	@Query("select new com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse(s.pagare.idTblAmortizacion,s.inversionista.persona.nombres,s.proyecto.empresa.pais.pais,s.proyecto.empresa.nombre,s.proyecto.idProyecto,s.pagare.montoInversion,"
			+ " s.pagare.fechaCreacion, s.pagare.plazo, s.pagare.rendimientoNeto, s.pagare.rendimientoTotalInversion,s.pagare.totalRecibir,s.pagare.fechaEfectiva) "
			+ " from Solicitud s where s.numeroSolicitud=:numeroSolicitud ")
	AmortizacionResponse pagarePorSolicitud(Long numeroSolicitud);
	
	@Query("select new com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse(s.amortizacion.idTblAmortizacion,s.empresa.nombre,s.empresa.pais.pais,s.empresa.nombre,s.idProyecto,s.amortizacion.montoInversion,"
			+ " s.amortizacion.fechaCreacion, s.amortizacion.plazo, s.amortizacion.rendimientoNeto, s.amortizacion.rendimientoTotalInversion,s.amortizacion.totalRecibir,s.amortizacion.fechaEfectiva) "
			+ " from Proyecto s where s.idProyecto=:cod")
	AmortizacionResponse amortizacionPorProyecto(String cod);
	
	
	@Query("select new com.multiplos.cuentas.pojo.amortizacion.AmortizacionDetalleResponse(d.detalleCobro,d.fechaEstimacion,d.rendimientoMensual,d.saldoCapital,d.cobrosCapital,d.totalRecibir,d.cuota,d.estadoPago,d.rutaPago)"
			+ " from DetalleAmortizacion d where d.tablaAmortizacion.idTblAmortizacion=:tabla ")
	List<AmortizacionDetalleResponse> consultaPorTabla(Long tabla);
	
	
	
	
	@Query("select s.amortizacion from  Solicitud s where s.numeroSolicitud = :numeroSolicitud")
	TablaAmortizacion consultaTablaAmortizacionPorNumSolicitud(Long numeroSolicitud);
	
	@Query(value = "select new com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse(s.amortizacion.idTblAmortizacion, s.amortizacion.fechaEfectiva, "
			  +" s.amortizacion.fechaCreacion,s.amortizacion.montoInversion,s.amortizacion.plazo,s.amortizacion.rendimientoNeto,s.amortizacion.rendimientoTotalInversion,s.amortizacion.totalRecibir) "
			  +" from Solicitud s where s.numeroSolicitud=:numeroSolicitud  ")
	TblAmortizacionResponse consultaTblAmortizacionFilter(Long numeroSolicitud);

	@Query("select p.amortizacion from Proyecto p where p.idProyecto=?1 and p.amortizacion.tipoTabla.idTipoTabla=?2 and p.amortizacion.estado='A'")
	TablaAmortizacion consultaTablaAmortizacionPorCodProyecto(String codigoProyecto, Long idTipoTabla);
	
	/*@Query("delete dt from DetalleAmortizacion where idDetAmortizacion=:idTa")
	void DeleteDetalleAmortizacion(Long idTa);*/
	
	
	@Modifying
	@Query(value="call inversion.pro_regenera_tbl_amortizacion(:cod_proyecto,:tipo_tabla,:f_efectiva) ", nativeQuery = true)
	void actualizaFechasCobro(@Param("cod_proyecto") String cod_proyecto,
							@Param("tipo_tabla") Long tipo_tabla,
							@Param("f_efectiva") LocalDate f_efectiva);
}
