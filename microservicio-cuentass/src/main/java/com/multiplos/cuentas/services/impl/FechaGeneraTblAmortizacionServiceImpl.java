package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.FechaGeneraTblAmortizacion;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.PorcentajeInteresProyecto;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.models.TipoTabla;
import com.multiplos.cuentas.pojo.amortizacion.DatosAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.FechaGenTblAmortRequest;
import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersona;
import com.multiplos.cuentas.repository.FechaGeneraTblAmortizacionRepository;
import com.multiplos.cuentas.services.FechaGeneraTblAmortizacionService;
import com.multiplos.cuentas.services.PorcentajeInteresProyectoService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.services.TipoTablaService;

@Service
public class FechaGeneraTblAmortizacionServiceImpl implements FechaGeneraTblAmortizacionService {

	private static final Logger LOG = LoggerFactory.getLogger(FechaGeneraTblAmortizacionServiceImpl.class);
	
	private FechaGeneraTblAmortizacionRepository respository;
	private SolicitudService solicitudService;
	private ProyectoService proyectoService;
	private TablaAmortizacionService tblAmortizacionService;
	private PorcentajeInteresProyectoService porcInteresProyectoService;
	private TipoTablaService tipoTablaService;
	
	
	@Autowired
	public FechaGeneraTblAmortizacionServiceImpl(FechaGeneraTblAmortizacionRepository respository, SolicitudService solicitudService,
			ProyectoService proyectoService,TablaAmortizacionService tblAmortizacionService,PorcentajeInteresProyectoService porcInteresProyectoService,
			TipoTablaService tipoTablaService) {
		this.respository = respository;
		this.solicitudService = solicitudService;
		this.proyectoService = proyectoService;
		this.tblAmortizacionService = tblAmortizacionService;
		this.porcInteresProyectoService = porcInteresProyectoService;
		this.tipoTablaService = tipoTablaService;
	}
	
	@Override
	@Transactional
	public Boolean validarProyecto(String codProyect)throws Exception{
		Proyecto proyecto=null;
		String response;
		FechaGeneraTblAmortizacion fechaGenTbl = new FechaGeneraTblAmortizacion();
		proyecto = proyectoService.consultaProyecto(codProyect);
		if(proyecto==null) {throw new Exception("No existe Proyecto con codigo "+codProyect);}
		proyectoService.validarProyectoAntesDeFechaEfectiva(codProyect); 
		
		fechaGenTbl = this.consultaFechaGenTblPorProyecto(codProyect);
		if(fechaGenTbl != null) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public String guardaFechaGeneracionAmortizacion(FechaGenTblAmortRequest fechaGenTblAmortizacion)throws Exception {
		String response;
		FechaGeneraTblAmortizacion fechaGenTbl = new FechaGeneraTblAmortizacion();
		boolean existeSolicitudes;
		Proyecto proyecto = new Proyecto();
		
		if(fechaGenTblAmortizacion.getFechaGeneracion().isBefore(LocalDate.now())) {
			response = "Error la fecha generación ingresada es incorrecta, la fecha no debe ser anterior";
			LOG.error(response);
			throw new Exception("Error la fecha generación ingresada es incorrecta, la fecha no debe ser anterior");}
		
			proyecto = proyectoService.consultaProyecto(fechaGenTblAmortizacion.getCodigoProyecto());
			
			if(proyecto == null) {response = "Error no existe proyecto, código operación: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());}
				
			fechaGenTbl = this.consultaFechaGenTblPorProyecto(fechaGenTblAmortizacion.getCodigoProyecto());
				if(fechaGenTbl != null) {
					response = "Ya existe una fecha de generación de tabla amortización para el proyecto ".concat(fechaGenTblAmortizacion.getCodigoProyecto()).concat(", ")
							.concat("Fecha Generación: ").concat(fechaGenTbl.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					LOG.info(response);
				}else {
					//verifica si existen solicitudes en estado EN TRANSITO
					existeSolicitudes = solicitudService.existenSolicitudesPorProyectoyEstado(fechaGenTblAmortizacion.getCodigoProyecto(), "TN");
					if(existeSolicitudes) {
						response = "Existen Solicitudes pendientes de aprobar por parte de Gerencia. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
						LOG.info(response);
					}else {
						try {
							FechaGeneraTblAmortizacion datoGeneraTblAmort = new FechaGeneraTblAmortizacion();
							datoGeneraTblAmort.setFechaGeneracion(fechaGenTblAmortizacion.getFechaGeneracion());
							datoGeneraTblAmort.setUsuarioCreacion(fechaGenTblAmortizacion.getUsuario());
							datoGeneraTblAmort.setFechaCreacion(LocalDate.now());						
							datoGeneraTblAmort.setProyecto(proyecto);
								
							respository.save(datoGeneraTblAmort);
							response = "Fecha de generación de tabla amortización guardada exitosamente. ";
							LOG.info(response.concat(fechaGenTblAmortizacion.getCodigoProyecto()));
							
						}catch(Exception e) {
							LOG.error("Error al guardar fecha de generacion tabla de amortización. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto()).concat(" ").concat(e.getMessage()));
							return "Error al guardar datos. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
						}
					}	
				}
			
		
		
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public FechaGeneraTblAmortizacion consultaFechaGenTblPorProyecto(String idProyecto) {
		return respository.consultaFechaGenTblPorProyecto(idProyecto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> consultaProyectoFechaGenTblPorFecha(LocalDate fecha) {
		return respository.consultaProyectoFechaGenTblPorFecha(fecha);
	}

	@Override
	@Transactional
	public String actualizaFechaGeneracionAmortizacion(FechaGenTblAmortRequest fechaGenTblAmortizacion) {
		String response = null;
		FechaGeneraTblAmortizacion fechaGenTbl = new FechaGeneraTblAmortizacion();
		boolean existeSolicitudes;
		Proyecto proyecto = new Proyecto();
		
		try {
			if(fechaGenTblAmortizacion.getFechaGeneracion().isBefore(LocalDate.now())) {
				response = "Error la fecha generación ingresada es incorrecta, la fecha no debe ser anterior";
				LOG.error(response);
			}else {
				proyecto = proyectoService.consultaProyecto(fechaGenTblAmortizacion.getCodigoProyecto());
				if(proyecto != null) {
					//verifica si existen solicitudes en estado EN TRANSITO
					existeSolicitudes = solicitudService.existenSolicitudesPorProyectoyEstado(fechaGenTblAmortizacion.getCodigoProyecto(), "TN");
					if(existeSolicitudes) {
						response = "Existen Solicitudes pendientes de aprobar por parte de Gerencia. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
						LOG.info(response);
					}else {
						fechaGenTbl = this.consultaFechaGenTblPorProyecto(fechaGenTblAmortizacion.getCodigoProyecto());
						if(fechaGenTbl != null) {
							fechaGenTbl.setFechaCreacion(LocalDate.now());
							fechaGenTbl.setFechaGeneracion(fechaGenTblAmortizacion.getFechaGeneracion());
							fechaGenTbl.setUsuarioCreacion(fechaGenTblAmortizacion.getUsuario());
							respository.save(fechaGenTbl);
							
							response = "Fecha de generación de tabla amortización actualizada exitosamente. ";
							LOG.info(response.concat(fechaGenTblAmortizacion.getCodigoProyecto()));
						}else {
							
							response = "Error no existe fecha para actualizar el Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
							LOG.error(response);
						}
					}
				}else {
					response = "Error no existe proyecto, código operación: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
					LOG.info(response);
				}
			}
			
		}catch(Exception e) {
			LOG.error("Error al actualizar fecha de generacion tabla de amortización. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto()).concat(" ").concat(e.getMessage()));
			return "Error al actualizar datos. Proyecto: ".concat(fechaGenTblAmortizacion.getCodigoProyecto());
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public String procesoGeneraTablaAmortizacionMasiva(String tipo)throws Exception {
		String response = null;
		
		List<FechaGeneraTblAmortizacion> fechGenTblAmort = new ArrayList<>();
		List<DatosAmortizacion> listDatoAmort = new ArrayList<>();
		List<InversionSolPersona> invSolPers = new ArrayList<>();
		List<PorcentajeInteresProyecto> listPorcIntProy = new ArrayList<>(); 
		BigDecimal montoInversion;
		fechGenTblAmort = this.consultaFechaGenTblPorFechaCreacion(LocalDate.now());
		if(fechGenTblAmort.isEmpty()) {throw new Exception ("No existen Fechas Generadas"); }
			if(tipo.contains("put")) {
				//actualiza las fechas de cobro para todas las tablas de amortizaciones
				response = this.procesoActualizaFechaTablaAmortizacionMasiva(fechGenTblAmort);
			}else if(tipo.contains("post")) {
				for(FechaGeneraTblAmortizacion fgt : fechGenTblAmort) {
					LOG.info("FechaEfectiva: "+fgt.getFechaGeneracion());
					//consulta los porcentajes de las tbl de amortizacion
					listPorcIntProy = porcInteresProyectoService.consultaPorcentajesPorProyecto(fgt.getProyecto().getIdProyecto());
					for(PorcentajeInteresProyecto pip : listPorcIntProy) {
						LOG.info("tipo tabla, id: ".concat(pip.getTipoTabla().getIdTipoTabla().toString()));
						
						
						if(pip.getTipoTabla().getIdTipoTabla() == 2) {//TABLA AMORTIZACION PAGARE
							LOG.info("Genera tabla - pagare");
							//enviara a generar y guardar tabla de amortizacion
							invSolPers = solicitudService.consultaSolPersonaPorProyectoYEstado(fgt.getProyecto().getIdProyecto(), "SATF");
							if(invSolPers.isEmpty()) { throw new Exception("Error no existe Solicitudes en estado Aprabada para tranferir fondos del "
									+ "proyecto "+fgt.getProyecto().getIdProyecto());  }
								List<DetalleAmortizacion> detalle;
								for (InversionSolPersona inv :invSolPers){
									LOG.info("1");
									TblAmortizacionResponse datoTblAmort = new TblAmortizacionResponse();
									Solicitud sol = new Solicitud();
									datoTblAmort = tblAmortizacionService.consultaTblAmortizacionFilter(inv.getNumeroSolicitud());
									sol=this.solicitudService.findById(inv.getNumeroSolicitud());
									LOG.info(""+sol.getNumeroSolicitud());
									//	if((datoTblAmort != null && datoTblAmort.getFechaEfectiva() == null) || (datoTblAmort == null)) {
										TablaAmortizacion datoAmort = new TablaAmortizacion();
										//datoAmort.setCodigoProyecto(fgt.getProyecto().getIdProyecto());
										datoAmort.setFechaEfectiva(fgt.getFechaGeneracion());
										datoAmort.setMontoInversion (inv.getMontoInversion());
										//datoAmort.setNumeroSolictud(inv.getNumeroSolicitud().toString());
										datoAmort.setPlazo(fgt.getProyecto().getPlazo());
										datoAmort.setRendimientoNeto(pip.getPorcentajeInteres());
										//datoAmort.setUsuarioCreacion(new Cuenta(fgt.getUsuarioCreacion()));
										datoAmort.setTipoTabla(pip.getTipoTabla());
										//listDatoAmort.add(datoAmort);
										detalle = tblAmortizacionService.generaDetalleTblAmortizacionOfi(pip.getProyecto().getPeriodoPago(),datoAmort.getMontoInversion().doubleValue(),
												datoAmort.getPlazo(),datoAmort.getRendimientoNeto().doubleValue(),LocalDate.now(),datoAmort);
										
										datoAmort.setDetallesTblAmortizacion(detalle);
										int lastIdx = detalle.size() - 1;
										datoAmort.setRendimientoTotalInversion(detalle.get(lastIdx).getRendimientoMensual());
										datoAmort.setTotalRecibir(detalle.get(lastIdx).getTotalRecibir());
										
									sol.setPagare(datoAmort);

//									HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
//									historialSol.setSolicitud(sol);
//									historialSol.setTablaCambiar("Pagare");
//									historialSol.setValorActual("Pagare");
//									historialSol.setValorAnterior("Vacio");
//									historialSol.setObservacion("Se crea pagare");
//									historialSol.setUsuarioModificacionInterno(new CuentaInterno(fgt.getUsuarioCreacion()));

									LOG.info("seteo pagare");
									HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
									historialSol.setSolicitud(sol);
									historialSol.setTablaCambiar("Pagare");
									historialSol.setValorActual(sol.getAmortizacion().getIdTblAmortizacion().toString());
									historialSol.setValorAnterior("Vacio");
									historialSol.setObservacion("Se general pagare para la solicitud");
									//historialSol.setUsuarioModificacionInterno(new CuentaInterno(fgt.getUsuarioCreacion()));

									sol.setHistorial(historialSol);
									LOG.info("seteo historial");
									this.solicitudService.guardaSolicitud(sol);
									LOG.info("sol guardada");
								}					

							

//							if(!listDatoAmort.isEmpty()) {
//								LOG.info("envia a generacionTablaAmortizacionMasiva");
//								//enviar a guardar tabla de amortizacion
//								tblAmortizacionService.generacionTablaAmortizacionMasiva(listDatoAmort);
//							}
							
						}
						else if(pip.getTipoTabla().getIdTipoTabla() == 3) {//TABLA AMORTIZACION GLOBAL
							LOG.info("Genera tabla - global promotor");
							listDatoAmort.clear();
							montoInversion = solicitudService.consultaMontoRecaudadoPorProyectoyEstado(fgt.getProyecto().getIdProyecto(), "SATF");
							List<DetalleAmortizacion> detalle;
							if(montoInversion.doubleValue() > 0) {
								TablaAmortizacion datoTblAmort = new TablaAmortizacion();
									datoTblAmort.setFechaEfectiva(fgt.getFechaGeneracion());
									datoTblAmort.setMontoInversion(montoInversion);
									datoTblAmort.setPlazo(fgt.getProyecto().getPlazo());
									datoTblAmort.setRendimientoNeto(pip.getPorcentajeInteres());
									//datoTblAmort.setUsuarioCreacion(new Cuenta( "ANALISTA"));
									datoTblAmort.setTipoTabla(pip.getTipoTabla());
								LOG.info("tbl");
								Proyecto aux ;
								detalle = tblAmortizacionService.generaDetalleTblAmortizacionOfi(pip.getProyecto().getPeriodoPago(),datoTblAmort.getMontoInversion().doubleValue(),
										datoTblAmort.getPlazo(),datoTblAmort.getRendimientoNeto().doubleValue(),LocalDate.now(),datoTblAmort);
								datoTblAmort.setDetallesTblAmortizacion(detalle);
								int lastIdx = detalle.size() - 1;
								datoTblAmort.setRendimientoTotalInversion(detalle.get(lastIdx).getRendimientoMensual());
								datoTblAmort.setTotalRecibir(detalle.get(lastIdx).getTotalRecibir());
								aux=proyectoService.consultaProyecto(fgt.getProyecto().getIdProyecto());
								if(aux==null) {throw new Exception("No existe Proyecto con código"+fgt.getProyecto().getIdProyecto());}
									aux.setAmortizacion(datoTblAmort);
									proyectoService.guardar(aux);
									}		
						}

						listDatoAmort.clear();
						invSolPers.clear();	

					}
					listPorcIntProy.clear();
					
					//enviar a actualizar la tabla de amortizacion de los inversionista(fechas cobro)
					//se envian => codigoProyecto IdTipoTabla fechaGeneracion
					tblAmortizacionService.procedimientoActualizaFechasCobro(fgt.getProyecto().getIdProyecto(), (long) 1, fgt.getFechaGeneracion());
					response = "Se han generado las tablas con la fecha efectiva";
					//response = "Proceso terminado.";
				}
				fechGenTblAmort.clear();
			}
		
		
		return response;		
	}
	
	public String procesoActualizaFechaTablaAmortizacionMasiva(List<FechaGeneraTblAmortizacion> fechGenTblAmort) {
		String response = null;
		List<TipoTabla> tipoTabla = new ArrayList<>();
		
		for(FechaGeneraTblAmortizacion fgt : fechGenTblAmort) {
			
			tipoTabla = tipoTablaService.findAll();
			if(!tipoTabla.isEmpty()) {
				tipoTabla.stream().forEach(t ->{
					LOG.info("FechaEfectiva: "+fgt.getFechaGeneracion()+" tipoTabla: "+t.getIdTipoTabla()+" proyecto: "+fgt.getProyecto().getIdProyecto());
					tblAmortizacionService.procedimientoActualizaFechasCobro(fgt.getProyecto().getIdProyecto(), t.getIdTipoTabla(), fgt.getFechaGeneracion());
				});
				response = "Proceso terminado.";
			}else {
				response = "Error no existen tipos de tabla amortización.";
				LOG.error(response);
			}
		}
		return response;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<FechaGeneraTblAmortizacion> consultaFechaGenTblPorFechaCreacion(LocalDate fecha) {
		return respository.consultaFechaGenTblPorFechaCreacion(fecha);
	}
	
}
