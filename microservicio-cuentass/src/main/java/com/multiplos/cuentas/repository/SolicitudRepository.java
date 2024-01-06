package com.multiplos.cuentas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.SolicitudDocumentos;
import com.multiplos.cuentas.models.SolicitudIds;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoSolicitud;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersona;

import com.multiplos.cuentas.reportes.ReportePorProyecto;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerencia;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoInversionista;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse.SolicitudResponseBuilder;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long>{
	
	

	SolicitudResponse solires = new SolicitudResponse();
	
	

	
	////////////////////////////////////////REPOSITORIO PARA INVESTOR//////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse(s.numeroSolicitud, s.proyecto.empresa.nombre, s.estadoActual.descripcion,s.proyecto.idProyecto) "
			  +" from Solicitud s where "
		      +" s.inversionista.idCuenta = :identificacion "
		      +" and (:numeroSolicitud is null or s.numeroSolicitud=:numeroSolicitud)"
		      +" and (:codProyecto is null or s.proyecto.idProyecto=:codProyecto)"
		      +" and (:idCalificacion is null or s.proyecto.calificacion=:idCalificacion)"
			  +" and (:idActividad is null or s.proyecto.empresa.actividad.idActividad=:idActividad)"
		      +" and s.estadoActual.idEstado = 'BO'  ")
	List<InversionEnProcesoResponse> consultaSolEnProceso( String identificacion,String codProyecto,Long numeroSolicitud, Long idCalificacion, Long idActividad);
	
	
	@Query("select new com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse(s.datosInversion.tablaAmortizacion,s.datosInversion.formulario,s.datosInversion.documentacion,s.datosInversion.pago) from Solicitud s  where s.numeroSolicitud =:numSol " )
	FormDatosIngresadoResponse ContinuarProceso(Long numSol);
	
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse(s.numeroSolicitud, s.proyecto.empresa.nombre, s.amortizacion.montoInversion,s.proyecto.plazo, s.estadoActual.descripcion, "
			  +"  s.observacion, s.proyecto.idProyecto) "
			  +" from Solicitud s  where "
			  +" s.inversionista.idCuenta = :identificacion "
			  +" and (:numeroSolicitud is null or s.numeroSolicitud=:numeroSolicitud)"
			  +" and (:codProyecto is null or s.proyecto.idProyecto=:codProyecto)"
			  +" and (:idCalificacion is null or s.proyecto.calificacion=:idCalificacion)"
			  +" and (:idActividad is null or s.proyecto.empresa.actividad.idActividad=:idActividad)"
		      +" and s.estadoActual.idEstado in ('PC','PAP') and s.amortizacion.estado='A' ")
	List<InversionPorConfirmarResponse> consultaSolPorConfirmar(String identificacion,String codProyecto,Long numeroSolicitud, Long idCalificacion, Long idActividad);
	
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoInversionista(s.numeroSolicitud, p.empresa.nombre, t.montoInversion, t.plazo, s.estadoActual.descripcion, "
			  +" s.proyecto.idProyecto, tr.monto, p.montoSolicitado) "
			  +" from Solicitud s, Proyecto p, TablaAmortizacion t, Transaccion tr where s.proyecto.idProyecto = p.idProyecto and s.amortizacion.idTblAmortizacion = t.idTblAmortizacion and s.numeroSolicitud = tr.solicitud.numeroSolicitud "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:numeroSolicitud is null or s.numeroSolicitud = :numeroSolicitud) and (:idTipoCalificacion is null or p.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or p.empresa.actividad.idActividad = :idActividad) "
		      +" and s.estadoActual.idEstado in ('TN','SATF','SFC','STF')  and t.estado='A' and tr.estado='A'"
		      +" and s.inversionista.idCuenta = :identificacion and t.tipoTabla.idTipoTabla=1")
	List<InversionEnTransitoInversionista> consultaSolEnTransitoInversionista(String codigoProyecto, Long numeroSolicitud, Long idTipoCalificacion, Long idActividad, String identificacion);
	
	@Query("select new com.multiplos.cuentas.pojo.plantilla.solicitud.DatoSolicitud(s.inversionista.idCuenta, s.inversionista.persona.nombres,s.numeroSolicitud,s.proyecto.empresa.nombre,s.proyecto.idProyecto,s.amortizacion.montoInversion,s.amortizacion.plazo,s.amortizacion.rendimientoNeto,s.inversionista.email)"
			+ " from Solicitud s where s.numeroSolicitud=:numSol")
	DatoSolicitud consultaDatoSolicitud(Long numSol);
	
	
	
	@Query(value = "select s from Solicitud s where s.inversionista.idCuenta=?1 and s.estadoActual.idEstado=?2 ")
	List<Solicitud> consultaSolicitudesPorPersonaYEstado(String identificacion, String estado);
	
    @Query(value = "select s from Solicitud s where s.proyecto.idProyecto=?1 ")
	List<Solicitud> consultaSolicitudPorProyecto(String codigoProyecto);
	
	@Query(value = "select s from Solicitud s "
			+ "where s.proyecto.idProyecto=?1 "
			+ " and s.estadoActual =?2")
	List<Solicitud> consultaSolicitudPorProyectoyEstado(String codigoProyecto,TipoEstado estadoActual);
	
	@Query(value = "select count(s) from Solicitud s where s.proyecto.idProyecto=?1 and s.inversionista.idCuenta=?2 and s.estadoActual.idEstado='BO' ")
	int consultaSolicitudExistente(String codigoProyecto, String identificacion);
	
	@Query(value = "select s.datosInversion.idDato from Solicitud s where s.numeroSolicitud=?1 and s.estadoActual.idEstado='BO' ")
	Long consultaDatoInversion(Long numeroSolicitud);
	
	@Query(value = "select s.documentos from Solicitud s where s.numeroSolicitud=?1 and s.documentos.estado ='A'")
	SolicitudDocumentos consultaDocumentos(Long numeroSolicitud);
	

	@Modifying
	@Query("update Solicitud s set s.aceptaLicitudFondos=?1, s.aceptaInformacionCorrecta=?2 where s.numeroSolicitud = ?3 ")
	void updateLicitudYInfoCorrecta(String aceptaLicitud, String aceptaInfoCorecta, String numeroSolicitud);
	
	@Query(value = "select count(s) from Solicitud s where s.inversionista.idCuenta=?1 ")
	int tieneSolicitudes(String identificacion);
	
	@Query("select s from Solicitud s where s.numeroSolicitud= :numeroSolicitud ")
	Solicitud consultaSolicitud(Long numeroSolicitud);
	
	
	
	@Query(value = "select s from Solicitud s where s.numeroSolicitud=?1  and s.estadoActual.idEstado=?2")
	Solicitud consultaSolicitudyEstado(String numeroSolicitud,String estado);
	
	@Query(value = "select count(s) from Solicitud s where (:identificacion is null or s.inversionista.idCuenta = :identificacion) "
			      +" and (:numeroSolicitud is null or s.numeroSolicitud = :numeroSolicitud) ")
	int tieneSolicitud(Long numeroSolicitud, String identificacion);
	
	
	
	//daviid consulta para solicitud filter
	@Query("select new com.multiplos.cuentas.pojo.solicitud.SolicitudResponse(s.numeroSolicitud,s.inversionista.persona.identificacion, s.proyecto.empresa.nombre, s.amortizacion.montoInversion, s.amortizacion.plazo, s.estadoActual.descripcion,"
			+ " s.proyecto.idProyecto,tr.monto,s.proyecto.montoSolicitado) "
			  +" from Solicitud s, Transaccion tr "
			  +" where s.numeroSolicitud = tr.solicitud.numeroSolicitud "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:numeroSolicitud is null or s.numeroSolicitud = :numeroSolicitud) "
		      +" and (:idTipoCalificacion is null or s.proyecto.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or s.proyecto.empresa.actividad.idActividad = :idActividad) "
		      +" and (:identificacion is null or s.inversionista.idCuenta = :identificacion) "
		      +" and s.estadoActual.idEstado =:estado "
		      +" and tr.estado='A'"
		      +" ORDER BY s.numeroSolicitud asc")
	List<SolicitudResponse>consultaSolicitudByFilter(String codigoProyecto, Long numeroSolicitud, Long idTipoCalificacion, Long idActividad, String identificacion,String estado);
	
	//daviid consulta para solicitud filter
//		@Query("select com.multiplos.cuentas.pojo.solicitud.SolicitudResponse.builder().identificacion(s.inversionista.persona.identificacion).build() "
//
////				+ " .identificacion(s.inversionista.persona.identificacion)"
////				+ " .nombreEmpresa(s.proyecto.empresa.nombre)"
////				+ " .inversion(s.amortizacion.montoInversion)"
////				+ " .plazo(s.proyecto.plazo)"
////				+ " .estado(s.estadoActual.idEstado)"
////				+ " .codProyecto(s.proyecto.idProyecto)"
////				+ " .montoPago(tr.monto)"
////				+ " .montoProyecto(s.proyecto.montoSolicitado)"
////				+ " .build() "
//
//				  +" from Solicitud s, Transaccion tr "
//				  +" where s.numeroSolicitud = tr.solicitud.numeroSolicitud "
//				  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
//			      +" and (:numeroSolicitud is null or s.numeroSolicitud = :numeroSolicitud) "
//			      +" and (:idTipoCalificacion is null or s.proyecto.calificacion.idTipoCalificacion = :idTipoCalificacion) "
//			      +" and (:idActividad is null or s.proyecto.empresa.actividad.idActividad = :idActividad) "
//			      +" and (:identificacion is null or s.inversionista.idCuenta = :identificacion) "
//			      +" and s.estadoActual.idEstado =:estado "
//			      +" and tr.estado='A'"
//			      +" ORDER BY s.numeroSolicitud asc")
//		List<SolicitudResponse>consultaSolicitudByFilter(String codigoProyecto, Long numeroSolicitud, Long idTipoCalificacion, Long idActividad, String identificacion,String estado);
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.responseManagerOper.SolicitudPapResponse(s.numeroSolicitud,s.inversionista.persona.identificacion, s.proyecto.empresa.nombre, s.amortizacion.montoInversion, s.amortizacion.plazo, s.estadoActual.descripcion, "
			  +" s.proyecto.idProyecto,s.proyecto.montoSolicitado) "
			  +" from Solicitud s"
			  +" where (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:numeroSolicitud is null or s.numeroSolicitud = :numeroSolicitud)"
		      +" and (:idTipoCalificacion is null or s.proyecto.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or s.proyecto.empresa.actividad.idActividad = :idActividad) "
		      +" and (:identificacion is null or s.inversionista.idCuenta = :identificacion)"
		      +" and s.estadoActual.idEstado =:estado "
		      +" ORDER BY s.numeroSolicitud asc")
	List<?>consultaSolicitudPapFilter(String codigoProyecto, Long numeroSolicitud, Long idTipoCalificacion, Long idActividad, String identificacion,String estado);
	
	
	
	
	
	
	
	@Query("select s from Solicitud s where s.numeroSolicitud = ?1")
	List<Solicitud> consultaHistorialEstadosSolicitud(String numeroSolicitud, Sort sort);

	@Query("select count(s) from Solicitud s where (:identificacion is null or s.inversionista.idCuenta = :identificacion) and s.estadoActual.idEstado=:estado ")
	int consultaCantidadSolicitudes(String identificacion, String estado);
	
	
	
	
	
	///////////////////////////////////REPOSITORIO PARA ANALISTA RPA//////////////////////////////////////

	
	
	
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerencia(s.proyecto.idProyecto, p.empresa.nombre, p.montoSolicitado, p.plazo, "
			  +" s.estadoActual.descripcion ) "
			  +" from Solicitud s, Proyecto p "
			  +" where s.proyecto.idProyecto = p.idProyecto "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:idTipoCalificacion is null or p.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or p.empresa.actividad.idActividad = :idActividad) "
		      +" and s.estadoActual.idEstado = :estadoActual  "
		      +" GROUP BY s.proyecto.idProyecto, p.empresa.nombre, p.montoSolicitado, p.plazo, s.estadoActual.descripcion")
	List<InversionEnTransitoGerencia> consultaSolPorEstadoGerencia(String codigoProyecto, Long idTipoCalificacion, Long idActividad, String estadoActual);


	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerencia(s.proyecto.idProyecto, p.empresa.nombre, p.montoSolicitado, p.plazo, "
			  +" s.estadoActual.descripcion ) "
			  +" from Solicitud s, Proyecto p "
			  +" where s.proyecto.idProyecto = p.idProyecto "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:idTipoCalificacion is null or p.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or p.empresa.actividad.idActividad = :idActividad) "
		      +" and s.estadoActual.idEstado = :estadoActual  "
		      +" GROUP BY s.proyecto.idProyecto, p.empresa.nombre, p.montoSolicitado, p.plazo, s.estadoActual.descripcion")
	List<InversionEnTransitoGerencia> consultaSolicitudesEnTransitoPorProyecto(String codigoProyecto, Long idTipoCalificacion, Long idActividad, String estadoActual);
	
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse(s.proyecto.idProyecto, p.empresa.nombre, s.numeroSolicitud, p.plazo, "
			  +"t.fechaCreacion, t.montoInversion, dt.cuota, dt.estadoPago, dt.fechaEstimacion, dt.idDetAmortizacion, dt.totalRecibir,s.usuarioCreacion.persona.identificacion) "
			  +" from Solicitud s, Proyecto p , TablaAmortizacion t, DetalleAmortizacion dt"
			  +" where s.proyecto.idProyecto = p.idProyecto "
			  +" and s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
			  +" and dt.tablaAmortizacion.idTblAmortizacion = t.idTblAmortizacion "
			  +" and t.tipoTabla.idTipoTabla = 1 "
			  +" and s.estadoActual.idEstado = 'VG' "
			  +" and dt.fechaEstimacion is not null "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
			  +" and dt.estadoPago ='Pendiente de pago' "
			  +" and dt.fechaEstimacion between current_date - 30 and current_date + 60  "
		      +" GROUP BY s.proyecto.idProyecto, p.empresa.nombre, s.numeroSolicitud, p.plazo, "
		      +" t.fechaCreacion, t.montoInversion, dt.cuota,"
		      +" dt.estadoPago, dt.fechaEstimacion, dt.idDetAmortizacion,s.usuarioCreacion.persona.identificacion")
	List<SolicitudVigenteResponse> consultaSolicitudVigente(String codigoProyecto);
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse(s.proyecto.idProyecto, p.empresa.nombre, s.numeroSolicitud, p.plazo, "
			  +"t.fechaCreacion, t.montoInversion, dt.cuota, dt.estadoPago, dt.fechaEstimacion, dt.idDetAmortizacion, dt.totalRecibir,s.usuarioCreacion.persona.identificacion) "
			  +" from Solicitud s, Proyecto p , TablaAmortizacion t, DetalleAmortizacion dt"
			  +" where s.proyecto.idProyecto = p.idProyecto "
			  +" and s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
			  +" and dt.tablaAmortizacion.idTblAmortizacion = t.idTblAmortizacion "
			  +" and (:codigoProyecto is null or s.proyecto.idProyecto = :codigoProyecto) "
			  +" and t.tipoTabla.idTipoTabla = 1 "
			  +" and s.estadoActual.idEstado = 'VG' "
			  +" and dt.fechaEstimacion is not null "
			  +" and dt.fechaEstimacion < current_date "
			  +" and dt.estadoPago is not 'Pendiente de pago' "
			  +" and dt.estadoPago is not 'Al dia' "
		      +" GROUP BY s.proyecto.idProyecto, p.empresa.nombre, s.numeroSolicitud, p.plazo, "
		      +" t.fechaCreacion, t.montoInversion, dt.cuota,"
		      +" dt.estadoPago, dt.fechaEstimacion, dt.idDetAmortizacion,s.usuarioCreacion.persona.identificacion")
	List<SolicitudVigenteResponse> consultaSolicitudVigenteVencidosyMora(String codigoProyecto);
	
	
	
	@Query("select coalesce(sum(t.montoInversion),0) from Solicitud s, TablaAmortizacion t "
			+ " where s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
			+ " and s.estadoActual.idEstado = ?2  "
			+ " and t.estado='A' and s.proyecto.idProyecto = ?1 and t.tipoTabla.idTipoTabla=1 ")
	BigDecimal consultaMontosInvertidosPorProyecto(String codigoProyecto, String estadoActual);
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersona(s.numeroSolicitud, t.montoInversion, s.inversionista.persona.tipoPersona, "
			+ " s.inversionista.persona.nombres, s.inversionista.persona.apellidos, s.inversionista.persona.razonSocial,s.inversionista.persona.identificacion) "
			+ " from Solicitud s, TablaAmortizacion t "
			 +" where s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
			+ " and s.estadoActual.idEstado = ?2  "
			+ " and t.estado='A' and s.proyecto.idProyecto = ?1 and t.tipoTabla.idTipoTabla=1")
	List<InversionSolPersona> consultaSolPersonaPorProyectoYEstado(String codigoProyecto, String estadoActual);
	
	

	//consulta montos de invercion que se encuentran en transito
//	@Query("select coalesce(sum(t.montoInversion),0) from Solicitud s, TablaAmortizacion t "
//			+ " where s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
//			+ " and s.estadoActual.idEstado in ('TN','SATF')  "
//			+ " and t.estado='A' and s.proyecto.idProyecto = ?1 and t.tipoTabla.idTipoTabla=1")
//	BigDecimal consultaMontoInvertidoPorProyectoInversionistas(String codigoProyecto);
	
	@Query("select count(s.numeroSolicitud) from Solicitud s where s.proyecto.idProyecto = ?1 and s.estadoActual.idEstado = ?2  ")
	int exiteSolicitudesPorProyectoyEstado(String codigoProyecto, String estadoActual);
	
	/*@Query("select s.numeroSolicitud from Solicitud s where s.estadoActual.idEstado=?1  and group by s.inversionista.idCuenta")
	List<String> consultaSolicitudesVigentesPorEstado(String estadoActual);*/
	
}
