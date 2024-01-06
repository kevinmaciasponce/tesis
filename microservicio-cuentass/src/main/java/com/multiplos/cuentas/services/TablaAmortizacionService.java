package com.multiplos.cuentas.services;

import java.time.LocalDate;
import java.util.List;

import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionRequest;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse;
import com.multiplos.cuentas.pojo.amortizacion.DatosAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorPrincipalResponse;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorRequest;

import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.UpdateSolicitudRequest;

public interface TablaAmortizacionService {

	//TablaResponse consultaAmortizacion(Long numSol);
	
	
	String creaSolicitudYTablaAmortizacion(SolicitudRequest solicitudRequest) throws Exception;
	TablaAmortizacion CreaTablaAmortizacion(AmortizacionRequest amortizacion)throws Exception;
	
	AmortizacionResponse consultaAmortizacionPorSolicitud(Long numSol)throws Exception;
	AmortizacionResponse consultaPagarePorSolicitud(Long numSol)throws Exception;
	AmortizacionResponse consultaAmortizacionPorProyecto(String cod)throws Exception;
	
	
	AmortizacionResponse simuladorTblAmortizacion(SimuladorRequest solicitudRequest)throws Exception;
	AmortizacionResponse consultaTablaAmortizacion(String numSolOCodProyecto, Long idTipoTabla);
	List<SimuladorPrincipalResponse> simuladorPrincipal(SimuladorRequest simulador)throws Exception;
	
	List<DetalleAmortizacion> generaDetalleTblAmortizacionOfi(int periodo,double montoInversion, int plazo, double rendimientoNeto, LocalDate fecha, TablaAmortizacion ta)throws Exception ;
	
	TablaAmortizacion consultaTblAmortizacionPorNumSolicitud(String numeroSolicitud);
	TblAmortizacionResponse consultaTblAmortizacionFilter(Long numeroSolicitud);	
	
	TablaAmortizacion consultaTblAmortizacionPorCodProyecto(String codigoPoryecto, Long idTipoTabla);
	AmortizacionResponse updateTblAmortizacion(UpdateSolicitudRequest solicitudRequest) throws Exception;
	//void generacionTablaAmortizacionMasiva(List<DatosAmortizacion> listDatoTbl)throws Exception;
	
	void procedimientoActualizaFechasCobro(String codigoProyecto, Long idTipoTabla, LocalDate fechaEfectiva);
	
}
