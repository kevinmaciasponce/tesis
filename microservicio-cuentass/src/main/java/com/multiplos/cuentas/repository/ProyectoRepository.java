package com.multiplos.cuentas.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.ProyectoRuta;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.promotor.CodigoYFecha;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;
import com.multiplos.cuentas.pojo.proyecto.ProyectoRutaResponse;
import com.multiplos.cuentas.pojo.proyecto.PruebaDestino;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;

public interface ProyectoRepository extends JpaRepository<Proyecto, String>, QueryByExampleExecutor<Proyecto> {

	@Query(" select new com.multiplos.cuentas.pojo.proyecto.PruebaDestino(p.destinoFinanciamiento , p.aceptaInformacionCorrecta ) from Proyecto p where p.idProyecto =:idProyecto ")
	PruebaDestino findByProyect(String idProyecto);
	
	
	@Query("select new com.multiplos.cuentas.pojo.proyecto.ProyectoResponse(" + " p.idProyecto, "
			+ " p.fechaInicioInversion, " + " p.fechaLimiteInversion," + " p.tasaEfectivaAnual," + " p.tipoInversion,"
			+ " p.destinoFinanciamiento," + " p.montoSolicitado," + " p.montoRecaudado," + "p.plazo,"
			+ " p.pagoInteres," + " p.pagoCapital,"
			+ " CASE WHEN p.indicadores is null THEN null ELSE (select i from Indicador i where i.idIndicador = p.indicadores.idIndicador) END, "
//			+ " CASE WHEN p.empresa is null THEN null ELSE (select i from Empresa i where i.idEmpresa = p.empresa.idEmpresa ) END "		
			+ " CASE WHEN  p.calificacion is null THEN null ELSE (select c from Calificacion c where c.idTipoCalificacion = p.calificacion.idTipoCalificacion) END,   " 
			+ " p.estadoActual.idEstado,"
			+ " p.empresa.idEmpresa"
//			+ " p.proyectoRutas"
//			+ " CASE WHEN p.empresa is null THEN null ELSE (select new com.multiplos.cuentas.pojo.promotor.EmpresaResponse( "
//				+ " py.idEmpresa, "
//				+ " py.nombre, "
//				+ " py.actividad.nombre,"
//				+ " py.ruc, "
//				+ " py.pais.pais ,"
//				+ " py.direccion, "
//				+ " py.ciudad, "
//				+ " CASE  WHEN  p.datosAnualActual is null  THEN null ELSE (select d.margenContribucion from EmpresaDatosAnuales d where d.empresa.idEmpresa = p.empresa.idEmpresa and d.activo=true) END, "
//				+ " CASE  WHEN  p.datosAnualActual is null  THEN null ELSE (select d.ventasTotales from EmpresaDatosAnuales d where d.empresa.idEmpresa = p.empresa.idEmpresa and d.activo=true) END, "
//				+ " py.descripcionProducto, "
//				+ " py.antecedente,"
//				+ " py.ventajaCompetitiva)"
//			+ " from Empresa py  "
//			+ " where py.idEmpresa = p.empresa.idEmpresa ) END "
//				+ " p.proyectoRutas "			
			+ " ) from Proyecto p where p.estadoActual.idEstado in ('EXI','PLQ','TF','FC','AV','PATF') order by p.fechaCreacion")
	List<ProyectoResponse> findAllProyectoResponse(PageRequest page);
	
	@Query("select new com.multiplos.cuentas.pojo.proyecto.ProyectoRutaResponse(r.nombre, r.ruta) from ProyectoRuta r where  r.proyecto.idProyecto =:idProyecto order by r.nombre")
	List<ProyectoRutaResponse> getRutasResponseByProyecto(String idProyecto);

	@Query("select p from ProyectoRuta p where p.proyecto.idProyecto=:idProyecto ")
	List<ProyectoRuta> getproyectoRutas(String idProyecto);
	
	
	@Query("select new com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa(p.idProyecto, p.empresa.nombre, p.montoSolicitado)"
			+ " from Proyecto p where p.estadoActual.idEstado = ?1")
	List<FilterEmpresa> consultaProyectosPorEstado(String estadoActual);

	@Query("select p from Proyecto p where p.idProyecto=?1 ")
	Proyecto consultaProyecto(String idProyecto);

	@Query("select p.periodoPago from Proyecto p where p.idProyecto=?1 ")
	int consultaPeriodoPago(String idProyecto);

	@Query(" select count(s) from Solicitud s where s.proyecto.idProyecto= :idProyecto "
			+ " and s.estadoActual.idEstado != 'BO' and s.estadoActual.idEstado != 'SATF' and s.estadoActual.idEstado != 'ANU'")
	int validarProyectoAntesDeFechaEfectiva(String idProyecto);

	@Query("select p.tasaEfectivaAnual from Proyecto p where p.idProyecto=?1 ")
	BigDecimal consultaTasaEfectiva(String idProyecto);

	@Query("select p.empresa.nombre from Proyecto p where p.idProyecto=?1")
	String consultaNombreEmpresa(String idProyecto);

	@Query("select p.plazo from Proyecto p where p.idProyecto=?1 ")
	int consultaPlazo(String idProyecto);

	@Query(value = "select new com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa(p.idProyecto,CONCAT(p.empresa.nombre,' R',p.ronda), p.montoSolicitado) "
			+ " from Proyecto p ")
	List<FilterEmpresa> consultaEmpresasActivas();

	@Query(value = "select new com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa(p.idProyecto, p.empresa.nombre, p.montoSolicitado) "
			+ " from Proyecto p where p.estadoActual=?1 ")
	List<FilterEmpresa> consultaEmpresasActivasxEstado(TipoEstado estadoActual);

	@Query(value = "select new com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa(p.idProyecto, p.empresa.nombre, p.montoSolicitado) "
			+ " from Proyecto p where p.idProyecto=?1 ")
	FilterEmpresa consultaEmpresa(String idProyecto);

	@Modifying
	@Query("update Proyecto p set p.estadoActual.idEstado=?1, p.estadoAnterior.idEstado=?2, p.usuarioModificacion=?3, p.fechaModificacion=?4 where p.idProyecto = ?5 ")
	void updateEstadosProyecto(String estadoActual, String estadoAnterior, String usuario,
			LocalDateTime fechaModificacion, String idProyecto);

	// consulta monto total del avance de la inversion por proyecto
	@Query("select coalesce(sum(t.montoInversion),0) from Solicitud s, TablaAmortizacion t "
			+ " where s.amortizacion.idTblAmortizacion = t.idTblAmortizacion "
			+ " and s.estadoActual.idEstado in ('TN','SATF','SFC','STF','VG','LQ') "
			+ " and t.estado='A' and s.proyecto.idProyecto = ?1 and t.tipoTabla.idTipoTabla=1")
	BigDecimal consultaMontoTotalInversionPorProyecto(String codigoProyecto);

	@Query("select new com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa(p.idProyecto, p.empresa.nombre, p.montoSolicitado)"
			+ " from Proyecto p where p.estadoActual.idEstado in ('AV','PATF')")
	List<FilterEmpresa> consultaProyectosEnAvance();

	// nueva
	@Query("select new com.multiplos.cuentas.pojo.proyecto.proyectosResponse( p.idProyecto, p.empresa.nombre, p.montoSolicitado, p.montoRecaudado, p.plazo,"
			+ " p.ronda,p.fechaInicioInversion,p.fechaLimiteInversion, p.tasaEfectivaAnual, p.tipoInversion, p.destinoFinanciamiento, "
			+ " p.pagoInteres,p.pagoCapital,p.calificacion.nombre, p.estadoActual.descripcion)" + " from Proyecto p "
			+ " where p.estadoActual.idEstado = :estado " + " and (:codProyect is null or p.idProyecto = :codProyect)"
			+ " ORDER BY p.fechaCreacion asc")
	List<proyectosResponse> consultaPorProyectoYEstado(String estado, String codProyect);

	@Query("select new com.multiplos.cuentas.pojo.proyecto.proyectosResponse( p.idProyecto, p.empresa.nombre, p.montoSolicitado, p.montoRecaudado, p.plazo,"
			+ " p.ronda,p.fechaInicioInversion,p.fechaLimiteInversion, p.tasaEfectivaAnual, p.tipoInversion, p.destinoFinanciamiento, "
			+ " p.pagoInteres,p.pagoCapital,p.calificacion.nombre, p.estadoActual.descripcion)" + " from Proyecto p "
			+ " where p.estadoActual.idEstado = :estado " + " and (:codProyecto is null or p.idProyecto = :codProyecto)"
			+ " and (:idTipoCalificacion is null or p.calificacion.idTipoCalificacion = :idTipoCalificacion)"
			+ " and (:idActividad is null or p.empresa.actividad.idActividad = :idActividad)"
			+ " ORDER BY p.fechaCreacion asc")
	List<proyectosResponse> consultaPorProyectoYEstado2(String estado, String codProyecto, Long idTipoCalificacion,
			Long idActividad);

	@Query("select new com.multiplos.cuentas.pojo.promotor.CodigoYFecha(p.idProyecto, p.fechaCreacion) from Proyecto p  order by p.fechaCreacion DESC")
	CodigoYFecha getCodeByFecha(PageRequest pageable);

	@Query("select new com.multiplos.cuentas.pojo.proyecto.proyectosResponse( "
			+ " p.idProyecto,"
			+ " p.empresa.nombre,"
			+ " p.montoSolicitado,"
			+ " p.montoRecaudado,"
			+ " p.plazo,"
			+ " p.ronda,"
			+ " p.fechaInicioInversion, "
			+ " p.fechaLimiteInversion,"
			+ " p.tasaEfectivaAnual,"
			+ " p.tipoInversion,"
			+ " p.destinoFinanciamiento, "
			+ " p.pagoInteres,"
			+ " p.pagoCapital,"
			+ " CASE WHEN p.calificacion is null THEN null else (select c.nombre from Calificacion c where c.idTipoCalificacion = p.calificacion.idTipoCalificacion) End,"
			+ " p.estadoActual.descripcion"
			+ " ) "
			+ " from Proyecto p "
			+ " where p.empresa.idEmpresa = :idEmpresa "
			+ " and (:estado is null or p.estadoActual.idEstado = :estado)")
	List<proyectosResponse> consultaProyectosResponse(Long idEmpresa, String estado);

	@Query("select new com.multiplos.cuentas.pojo.proyecto.proyectosResponse( p.idProyecto, p.empresa.nombre, p.montoSolicitado, p.montoRecaudado, p.plazo,"
			+ " p.ronda,p.fechaInicioInversion,p.fechaLimiteInversion, p.tasaEfectivaAnual, p.tipoInversion, p.destinoFinanciamiento, "
			+ " p.pagoInteres,p.pagoCapital,p.calificacion.nombre, p.estadoActual.descripcion)" + " from Proyecto p "
			+ " where p.idProyecto = :idProyecto ")
	proyectosResponse consultaProyectoResponse(String idProyecto);
}
