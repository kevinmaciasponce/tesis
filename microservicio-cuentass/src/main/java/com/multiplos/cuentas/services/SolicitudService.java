package com.multiplos.cuentas.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse;
import com.multiplos.cuentas.pojo.formulario.FormJurRequest;
import com.multiplos.cuentas.pojo.formulario.FormNatRequest;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.proyecto.ProyectoPorEstadoResponse;
import com.multiplos.cuentas.pojo.proyecto.UpdatePYSRequest;

import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoProyecto;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocPagoRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocumentoRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.CantidadSolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionAprobTransFondoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerenciaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionHistorialEstado;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersona;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.inversionResponse;

public interface SolicitudService {
	
	Solicitud guardaSolicitud(Solicitud solicitud);
	Solicitud findById(Long id)throws Exception;
	
	
	String updateAll(List<Solicitud> solicitudes, String nuevoEstado, Cuenta usuarioModificacion, String Observacion)throws Exception;
	
	//List<SolicitudResponse> consultaSolicitudesPorPersonaYEstado(String identificacion, String estado);
	//List<SolicitudResponse> consultaSolicitudPorProyecto(String codigoPoryecto);
	
	//consulta solicitudes filter
	List<?> consultaSolcitudesByFilter(FilterSolicitudRequest filterRequest,String campo, String valor);
	
	//response manager oper
	List<?> consultaSolcitudesPapFilter(FilterSolicitudRequest filterRequest,String campo, String valor);
	String aprobarSolicitudPap(Long numSol, String user, String campo, String valor)throws Exception ;
	String anularSolicitudPap(Long numSol, String user)throws Exception ;
	FormDatosIngresadoResponse continuarProceso(Long numSol);
	
	boolean consultaSolicitudExistente(String codigoProyecto, String identificacion);
	
	String guardaPersonaInfoAdicional(FormNatRequest requestNat, FormJurRequest requestJur, String tipo);
	String guardaDocumentoIdentificacion(String solicitud, MultipartFile file,MultipartFile filepost) throws Exception;
	String actualizaDocumentoFormulario(String solicitud, MultipartFile file,MultipartFile filepost) throws Exception;
	boolean tieneSolicitudes(String identificacion);
	//SolicitudResponse consultaSolicitud(Long numeroSolicitud);
	
	
	
	
	String guardaDocumentoComprobantePago(String solicitud, MultipartFile file, String tipoDocumento) throws Exception;
	DocIdentificacionResponse actualizaDocumentoComprobantePago(String solicitud, MultipartFile file, String tipoDocumento) throws Exception;
	List<Transaccion>  consultarDocumentoComprobantePago(Long numSolicitud)throws Exception;
	
	
	
	List<InversionEnProcesoResponse> consultaSolcitudesEnProceso(FilterSolicitudRequest filterInvRequest);
	List<InversionPorConfirmarResponse> consultaSolcitudesPorConfirmar(FilterSolicitudRequest filterInvRequest);
	
	
	/////////////////////////////////////////SERVICIOS PARA ANALISTA//////////////////////////////////////
//	List<TransaccionesPorConciliar> consultaSolcitudesPorConfirmarAnalista(
//			FilterIntSolicitudRequest filterAnalistaOpRequest);
	
	
	
	List<InversionHistorialEstado> consultaHistorialEstadosSolicitud(String numeroSolicitud);
	List<CantidadSolicitudResponse> consultaCantidadSolicitudes(String identificacion,String tipoCliente);
	String actualizaEstadoSolicitudes(List<Long> solicitud, TipoEstado estado, String usuario, String observacion)throws Exception;
	
	List<InversionEnTransitoGerenciaResponse> consultaSolicitudesEnTransitoGerencia(FilterProyectoRequest filterGerenciaRequest);
	List<ProyectoPorEstadoResponse> consultaSolicitudesAgrupadas(FilterProyectoRequest filterGerenciaRequest,String campo, String valor);
	InversionSolPersonaResponse consultaDatoSolPersonaPorProyectoYEstado(String codigoProyecto, String estadoActual);
	List<InversionAprobTransFondoResponse> consultaSolicitudesAprobTransFondos(FilterProyectoRequest filterGerenciaRequest);
	
	List<InversionEnTransitoResponse> consultaSolEnTransitoInversionista(FilterSolicitudRequest filterEntransitoInv);
	
	

	String updatePYS(UpdatePYSRequest cambiar) throws Exception;

	List<DatoProyecto> consultaProyectosEnTransito();
	boolean existenSolicitudesPorProyectoyEstado(String codigoProyecto, String estadoActual);
	
	List<InversionSolPersona> consultaSolPersonaPorProyectoYEstado(String codigoProyecto, String estadoActual);
	BigDecimal consultaMontoRecaudadoPorProyectoyEstado(String codigoProyecto, String estadoActual);
	//void enviaEmailSolicitudesPorEstado(String estadoActual);//falta ajustar el proceso
	String guardarDocumentosxSolicitud(Long solicitud,MultipartFile[] file,String observacion,String usuarioInterno)throws Exception;
	String aprobarProyectoaFirmaContrato(String codProyecto,String usuario)throws Exception;
	String updatePYS2(UpdatePYSRequest cambiar,MultipartFile file) throws Exception;
	List<SolicitudVigenteResponse> consultaVigente(FilterProyectoRequest filterRequest);
	String registraPagoVigente(Long numSolicitud,int cuota,LocalDate fechaRealizada,String usuarioModificacion,MultipartFile file)throws Exception;
	String guardacedula(SolicitudDocumentoRequest solDocIdentificacion, MultipartFile file,MultipartFile fileposterior)throws Exception;
	//String enviaEmailInversionFinalizada(String numeroSolicitud);//solo pruebas

	
}
