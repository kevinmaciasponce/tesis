package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.DetallePorcentajeSolicitudAprobada;
import com.multiplos.cuentas.models.PorcentajeSolicitudAprobada;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoProyecto;
import com.multiplos.cuentas.pojo.proyecto.PorcSolAprobadaRequest;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.pojo.solicitud.NumeroSolicitudRequest;
import com.multiplos.cuentas.repository.PorcentajeSolicitudAprobadaRepository;
import com.multiplos.cuentas.services.PorcentajeSolicitudAprobadaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TipoEstadoService;

@Service
public class PorcentajeSolicitudAprobadaServiceImpl implements PorcentajeSolicitudAprobadaService {

	private static final Logger LOG = LoggerFactory.getLogger(PorcentajeSolicitudAprobadaServiceImpl.class);
	
	private PorcentajeSolicitudAprobadaRepository porcSolAprobadaRespository;
	private ProyectoService proyectoService;
	private SolicitudService solicitudService;
	private TipoEstadoService tipoEstadoService;
	
	private DecimalFormatSymbols simb;
	private DecimalFormat df;
	
	@Autowired
	public PorcentajeSolicitudAprobadaServiceImpl(PorcentajeSolicitudAprobadaRepository porcSolAprobadaRespository,ProyectoService proyectoService,
			SolicitudService solicitudService, TipoEstadoService tipoEstadoService) {
		
		this.porcSolAprobadaRespository = porcSolAprobadaRespository;
		this.proyectoService = proyectoService;
		this.solicitudService = solicitudService;
		this.tipoEstadoService = tipoEstadoService;
		
		simb = new DecimalFormatSymbols();
        simb.setDecimalSeparator('.');
        simb.setGroupingSeparator(',');
        df = new DecimalFormat("#,###.00", simb);
	}
	
	@Override
	@Transactional
	public String guardaPorcentajeSolAprobada(PorcSolAprobadaRequest porcSolAprobada) throws Exception {
		String response;
		List<DetallePorcentajeSolicitudAprobada> listDetalleSolAprobada = new ArrayList<>();
		PorcentajeSolicitudAprobada porcSol = new PorcentajeSolicitudAprobada();
		List<Long> listNumSolicitudes = new ArrayList<>();
		Proyecto proyecto = new Proyecto();
		double montoSolicitado = 0;
		double montoAprobado = 0;
		double porcAprobado = 0;
		String resCambioEstado;
		try {
			if(!porcSolAprobada.getSolicitudes().isEmpty()) {
				//LOG.info("inicio");
				proyecto = proyectoService.consultaProyecto(porcSolAprobada.getCodigoProyecto());
				if(proyecto != null) {
					//LOG.info("exite proyecto");
					PorcentajeSolicitudAprobada porcSolAprob = new PorcentajeSolicitudAprobada();
					
					montoSolicitado = Double.parseDouble(porcSolAprobada.getMontoSolicitado().replace(",", ""));
					montoAprobado = Double.parseDouble(porcSolAprobada.getMontoAprobado().replace(",", ""));
					//LOG.info("consultaPorcSolAprobado: "+porcSolAprobada.getCodigoProyecto());
					porcSol = this.consultaPorcSolAprobado(porcSolAprobada.getCodigoProyecto());
					if(porcSol != null) {
						//LOG.info("entra consultaPorcSolAprobado");
						montoAprobado = montoAprobado + porcSol.getMontoAprobado().doubleValue();
						porcAprobado = (montoAprobado * 100) / montoSolicitado;
						
						porcSol.setMontoAprobado(new BigDecimal(montoAprobado));
						porcSol.setPorcentajeAprobado(new BigDecimal(porcAprobado).setScale(2, RoundingMode.HALF_UP));
						
						this.actualizaPorcentajeSolAprobada(porcSol, porcSolAprobada.getSolicitudes());
						
						porcSolAprobada.getSolicitudes().forEach(s ->{
							listNumSolicitudes.add(Long.parseLong( s.getNumeroSolicitud()));
						});
						
					}else {
						//LOG.info("else consultaPorcSolAprobado");
						porcAprobado = (montoAprobado * 100) / montoSolicitado;
						
						porcSolAprob.setMontoSolicitado(new BigDecimal(montoSolicitado));
						porcSolAprob.setMontoAprobado(new BigDecimal(montoAprobado));
						porcSolAprob.setPorcentajeAprobado(new BigDecimal(porcAprobado).setScale(2, RoundingMode.HALF_UP));
						porcSolAprob.setProyecto(proyecto);
						porcSolAprob.setFechaAprobacion(LocalDate.now());
						porcSolAprob.setUsuarioAprobacion(porcSolAprobada.getUsuario());
						porcSolAprob.setObservacion(porcSolAprobada.getObservacion());
						porcSolAprob.setFechaCreacion(LocalDateTime.now());
						
						porcSolAprobada.getSolicitudes().forEach(s ->{
							DetallePorcentajeSolicitudAprobada d = new DetallePorcentajeSolicitudAprobada();
							d.setNumeroSolicitud(s.getNumeroSolicitud());
							d.setPorcentajeSolicitudAprobada(porcSolAprob);
							d.setFechaCreacion(LocalDateTime.now());
							
							listDetalleSolAprobada.add(d);
							listNumSolicitudes.add( Long.parseLong(  s.getNumeroSolicitud()));
						});
					
						porcSolAprob.setDetallePorcSolAprobada(listDetalleSolAprobada);
						
						porcSolAprobadaRespository.save(porcSolAprob);
					}
					
					TipoEstado tipoEstado = new TipoEstado();
					
					//funcionalidad para cambio de estados del proyecto => de AV a PATF
					proyectoService.updateEstadoProyecto("PATF", proyecto.getEstadoActual().getIdEstado(), porcSolAprobada.getUsuario(), LocalDateTime.now(), proyecto.getIdProyecto());
					
					//funcionalidad para guardar historial de modificacion
					//***
					
					//funcionalidad para cambio de estados de las solicitudes => de TN a SATF
					tipoEstado = tipoEstadoService.findById("SATF");//ESTADO APROBADA TRANSFERIR FONDOS = SATF
					
					resCambioEstado = solicitudService.actualizaEstadoSolicitudes(listNumSolicitudes, tipoEstado, porcSolAprobada.getUsuario(), null);
					if(resCambioEstado.contains("Error")) {
						LOG.error("Error al actualizar estado APROBADA TRANSFERIR FONDOS. Codigo proyecto: "+porcSolAprobada.getCodigoProyecto());
					}
					
					response = "Se aprueba para el cierre de la operación por $ "+df.format(montoAprobado);
					LOG.info("Se aprueba para el cierre de la operación por $ "+df.format(montoAprobado));
					proyecto = null;
				}else {
					LOG.error("Error no existe proyecto de inversión: "+porcSolAprobada.getCodigoProyecto());
					throw new Exception("Error no existe proyecto de inversión");
				}
				
			}else {
				LOG.error("Error no se ha enviado las solicitudes a aprobar");
				throw new Exception("Error no se ha enviado las solicitudes a aprobar");
			}
		}catch(Exception e) {
			LOG.error("Error al guardar porcentaje aprobado "+e.getMessage());
			throw new Exception(e.getMessage());
		}

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public PorcentajeSolicitudAprobada consultaPorcSolAprobado(String codigoProyecto) {
		return porcSolAprobadaRespository.consultaPorcSolAprobado(codigoProyecto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DatoProyecto> consultaProyectosAprobadoTransferirFondo(LocalDate fechaAprobacion) {
		List<FilterEmpresa> proyectos = new ArrayList<>();
		List<DatoProyecto> datoProyectosAprob = new ArrayList<>();
		
		proyectos = proyectoService.consultaProyectosPorEstado("PATF");
		if(!proyectos.isEmpty()) {
			proyectos.forEach(p ->{
				PorcentajeSolicitudAprobada porcSol = new PorcentajeSolicitudAprobada();
				DatoProyecto proyectoAprob = new DatoProyecto();
				
				porcSol = this.consultaPorcSolAprobado(p.getCodProyecto());
				
				if(porcSol.getFechaAprobacion().equals(fechaAprobacion)) {
					proyectoAprob.setPorcentaje(porcSol.getPorcentajeAprobado());
					proyectoAprob.setNombreEmpresa(p.getNombreEmpresa());
					proyectoAprob.setCodigoProyecto(p.getCodProyecto());
					
					datoProyectosAprob.add(proyectoAprob);
				}
			});
		}else {
			datoProyectosAprob.clear();
		}
		return datoProyectosAprob;
	}

	@Transactional
	public void actualizaPorcentajeSolAprobada(PorcentajeSolicitudAprobada porcSolAprobada, List<NumeroSolicitudRequest> solicitudes) {
		List<DetallePorcentajeSolicitudAprobada> listDetalleSolAprobada = new ArrayList<>();
		
		for(NumeroSolicitudRequest s : solicitudes) {
			DetallePorcentajeSolicitudAprobada d = new DetallePorcentajeSolicitudAprobada();
			d.setNumeroSolicitud(s.getNumeroSolicitud());
			d.setPorcentajeSolicitudAprobada(porcSolAprobada);
			d.setFechaCreacion(LocalDateTime.now());
			
			listDetalleSolAprobada.add(d);
		}
		porcSolAprobada.setDetallePorcSolAprobada(listDetalleSolAprobada);
		porcSolAprobadaRespository.save(porcSolAprobada);
	}
	
}
