package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.transaccion.TransaccionResponse;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.repository.TablaAmortizacionRepository;
import com.multiplos.cuentas.repository.TransaccionRepository;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.services.TransaccionService;

@Service
public class TransaccionServiceImpl implements TransaccionService {

	private TransaccionRepository respository;
	private SolicitudRepository repositorySol;
	private TablaAmortizacionRepository repositoryTA;
	private TablaAmortizacionService serviceTA;
	@Autowired
	public TransaccionServiceImpl(TransaccionRepository respository,
			SolicitudRepository repositorySol,
			TablaAmortizacionRepository repositoryTA,
			TablaAmortizacionService serviceTA) {
		this.respository = respository;
		this.repositorySol= repositorySol;
		this.repositoryTA = repositoryTA;
		this.serviceTA = serviceTA;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TransaccionesPorConciliarResponse> consultaTransaccionesPorConciliar(FilterIntSolicitudRequest filterAnalistaOpRequest) {
		LocalDate fInicio = null;
		LocalDate fFin = null;
		/*
		if(filterAnalistaOpRequest.getFecha() != null) {
			fInicio = filterAnalistaOpRequest.getFecha();
			fFin = filterAnalistaOpRequest.getFecha();
		}else {
			fInicio = LocalDate.now().minusMonths(1);//ajustar para produccion
			fFin = LocalDate.now().plusDays(1);
		}
		*/
		return (List<TransaccionesPorConciliarResponse>) respository.findByConciliar(filterAnalistaOpRequest.getCodProyecto(),filterAnalistaOpRequest.getNumeroSolicitud(),filterAnalistaOpRequest.getIdTipoCalificacion(),filterAnalistaOpRequest.getIdActividad());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Transaccion> findBySolicitud(Long numeroSolicitud) {
		return respository.findBySolicitud(numeroSolicitud);
	}
	@Override
	@Transactional(readOnly = true)
	public List<TransaccionResponse> consultaPorSolicitud(Long numeroSolicitud)throws Exception {
		List<TransaccionResponse> response;
		try {
				response=respository.consultaPorSolicitud(numeroSolicitud);
			}catch (Exception e) {
				throw new Exception(e.getMessage());
			}
			return response;
	
		}
	

	@Override
	@Transactional
	public String guardaTransaccion(Transaccion transaccion) {
		try {
			respository.save(transaccion);
		}catch(Exception e) {
			return "Error no se pudo agregar la transacción";
		}
		return "ok";
	}

	@Override
	@Transactional(readOnly = true)
	public Transaccion consultaTransaccion(Long numeroSolicitud) {
		return respository.consultaTransaccionPorSolicitud(numeroSolicitud);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Transaccion> consultaTransaccionesPorConciliar() {
		return respository.consultaTransaccionesPorConciliar();
	}
	
	@Override
	@Transactional
	public String actualizarTransaccion(String usuario,Long numeroSolicitud, String numComprobante, String fecha,String monto,String observacion)throws Exception {
		
		
		if(numComprobante==null && fecha==null && monto==null) {throw new Exception("Datos Insuficientes" );}
		Solicitud sol;
		
		Transaccion tr= new Transaccion();
		TablaAmortizacion ta= new TablaAmortizacion();
		AmortizacionRequest amortRequest;
		
		//modificar la busqueda de transaccion
		tr = respository.consultaTransaccionPorSolicitud(numeroSolicitud );
		
		ta = (TablaAmortizacion)this.repositoryTA.consultaTablaAmortizacionPorNumSolicitud(numeroSolicitud);
		
		if(tr==null) {throw new Exception("No existe transacción para la solicitud: "+numeroSolicitud ); }
		if(ta==null) {throw new Exception("No existe Amortización para la solicitud: "+numeroSolicitud ); }
		
		try{
			if(monto!=null) {
				BigDecimal nuevoMonto= new BigDecimal(monto);
				tr.setMonto(nuevoMonto);
				sol= repositorySol.consultaSolicitud(numeroSolicitud );
				if(sol==null) {throw new Exception("Solicitud no Existe: " ); }
				amortRequest = new AmortizacionRequest();
				//amortRequest.setNumeroSolictud(sol.getNumeroSolicitud().toString());
				amortRequest.setCodigoProyecto(sol.getProyecto().getIdProyecto());
				amortRequest.setInversion(nuevoMonto.doubleValue());
				amortRequest.setUsuario(sol.getInversionista().getPersona().getNombres());
				amortRequest.setIdentificacion(sol.getInversionista().getIdCuenta());
				amortRequest.setPeriodoPago(sol.getProyecto().getPeriodoPago());
				amortRequest.setIdTipoTabla((long) 1);//1: tabla amortizacion inversionista
				this.repositoryTA.delete(ta);
				ta= this.serviceTA.CreaTablaAmortizacion(amortRequest);
				sol.setAmortizacion(ta);
				this.repositorySol.save(sol);
			}
		}catch(Exception mon){throw new Exception ("error en  monto "+mon.getMessage());}
		try{
			try {
				if(numComprobante!=null) {
					tr.setNumeroComprobante(numComprobante);
				}
				}catch(Exception fec){throw new Exception ("Error en número de comprobante");}
			try {
				if(fecha!=null) {
					tr.setFechaTransaccion( LocalDate.parse(fecha));
				}
			}catch(Exception fec){throw new Exception ("error formato de fecha");}
//		tr.setUsuarioModificacion(usuario);
//		tr.setFechaModificacion(LocalDateTime.now());
		
		respository.save(tr);
		
		}catch(Exception e){
			throw new Exception("Error no se pudo actualizar la transaccion" +e);
		}
		return "Transacción Actualizada";
	
	
	
}
}

