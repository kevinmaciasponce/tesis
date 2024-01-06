package com.multiplos.cuentas.repository;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.transaccion.TransaccionResponse;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

	@Query("select t from Transaccion t where t.solicitud.numeroSolicitud=:numeroSolicitud and t.estado='A'")
	List<Transaccion> findBySolicitud(Long numeroSolicitud);
	
	@Query("select new com.multiplos.cuentas.pojo.transaccion.TransaccionResponse("
			+ " t.solicitud.numeroSolicitud, t.numeroComprobante, t.rutaComprobante,"
			+ " t.usuarioCreacion.persona.nombres, t.depositante, t.fechaTransaccion,t.formaPago.descripcion,t.monto,t.estado)"
			+ " from Transaccion t where t.solicitud.numeroSolicitud=:numeroSolicitud ")
	List<TransaccionResponse> consultaPorSolicitud(Long numeroSolicitud);
	
	@Query("select t from Transaccion t where t.conciliado ='N'"
			+ " and t.estado='A'"
			+ " and t.solicitud.estadoActual.idEstado = 'PC' ")
	List<Transaccion> consultaTransaccionesPorConciliar( );
	
	@Query("select t from Transaccion t where t.solicitud.numeroSolicitud=?1 and t.estado='A'")
	Transaccion consultaTransaccionPorSolicitud(Long numeroSolicitud);
	
	@Query("select t from Transaccion t where t.numeroComprobante=?1 and t.estado='A'")
	Transaccion consultaTransaccionPorComprobante(String comprobante);
	
	
	
	///////////////////////////////////REPOSITORIO PARA ANALISTA RPA//////////////////////////////////////
	
	@Query("select new com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse( t.solicitud.numeroSolicitud, t.solicitud.inversionista.idCuenta,t.solicitud.proyecto.empresa.nombre, t.solicitud.amortizacion.montoInversion, t.solicitud.amortizacion.plazo, t.solicitud.estadoActual.descripcion, "
			  +" t.fechaTransaccion, t.solicitud.observacion, t.solicitud.proyecto.idProyecto, t.numeroComprobante, t.monto )"
			  +" from  Transaccion t where t.solicitud.estadoActual.idEstado = 'PC'"
			  +" and t.conciliado= 'N' "
			  +" and t.estado='A'"
			  +" and (:codigoProyecto is null or t.solicitud.proyecto.idProyecto = :codigoProyecto) "
		      +" and (:numeroSolicitud is null or t.solicitud.numeroSolicitud = :numeroSolicitud) "
		      +" and (:idTipoCalificacion is null or t.solicitud.proyecto.calificacion.idTipoCalificacion = :idTipoCalificacion) "
		      +" and (:idActividad is null or t.solicitud.proyecto.empresa.actividad.idActividad = :idActividad)  ")
	List<TransaccionesPorConciliarResponse> findByConciliar(String codigoProyecto, Long numeroSolicitud, Long idTipoCalificacion, Long idActividad);
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Query("select t.solicitud from Transaccion t where t.conciliado= 'S'"
			+ " and t.solicitud.estadoActual.idEstado = 'PC'"
			+ " and t.estado= 'A'"
			)
	List<Solicitud> consultaSolicitudPorTransaccionConciliada();
	
}