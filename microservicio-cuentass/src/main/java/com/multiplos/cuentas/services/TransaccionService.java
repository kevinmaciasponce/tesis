package com.multiplos.cuentas.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.transaccion.TransaccionResponse;

public interface TransaccionService {

	///////////////////SERVICIOS PARA ANALISTA//////////////////////////////
	
	List<TransaccionesPorConciliarResponse> consultaTransaccionesPorConciliar(
			FilterIntSolicitudRequest filterAnalistaOpRequest);
	List<Transaccion> consultaTransaccionesPorConciliar();
	
	
	List<Transaccion> findBySolicitud(Long numeroSolicitud);
	List<TransaccionResponse> consultaPorSolicitud(Long numeroSolicitud)throws Exception ;
	
	
	String guardaTransaccion(Transaccion transaccion);
	Transaccion consultaTransaccion(Long numeroSolicitud);
	String actualizarTransaccion(String usuario,Long numeroSolicitud, String numComprobante, String fecha,String monto,String observacion)throws Exception;
}
