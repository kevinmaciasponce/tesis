package com.multiplos.cuentas.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.HistorialDeProyecto;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.RangoPago;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.promotor.ProyectoFormulario;
import com.multiplos.cuentas.pojo.proyecto.ProyectoRequest;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;
import com.multiplos.cuentas.pojo.proyecto.PruebaDestino;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;

public interface ProyectoService {

	
	
	PruebaDestino findPrueba(String idProyecto)throws Exception;
	
	
	List<Proyecto> findAll();
	
	///////////////////////consulta proyectos para inversiones///////////
	List<ProyectoResponse> consultaProyectos(int partitions, int group)throws Exception;
	//////////////////////////////////////////////////////////////
	
	//////consultas proyectos response para ventanas ////////////
	List<proyectosResponse> consultasPorEstado( String estado, String codProyecto);
	List<proyectosResponse> consultasPorEstado2( String estado, FilterProyectoRequest filter);
	//////////////////////////////
	
	
	Proyecto consultaProyecto(String idProyecto)throws Exception;
	int consultaPeriodoPago(String idProyecto) throws CuentaException;
	boolean validarProyectoAntesDeFechaEfectiva(String cod) throws Exception;
	
	String guardar(Proyecto proyecto);
	Object putProyecto(ProyectoFormulario form)throws Exception;
	Object getProyectoResponse(ProyectoRequest filter)throws Exception;
	
	BigDecimal consultaTasaEfectiva(String idProyecto);
	String consultaNombreProyecto(String idProyecto);
	int consultaPlazo(String idProyecto);
	List<FilterEmpresa> filterEmpresas();
	List<FilterEmpresa> filterEmpresasxEstado(TipoEstado estadoActual);
	FilterEmpresa consultaEmpresa(String idProyecto);
	void updateEstadoProyecto(String estadoActual, String estadoAnterior, String usuario, LocalDateTime fechaModificacion, String idProyecto);
	List<FilterEmpresa> consultaProyectosPorEstado(String estadoActual);
	String updateProyecto(HistorialDeProyecto history);
	List<FilterEmpresa> consultaProyectosEnAvance();
	
	proyectosResponse consultarProyectoResponse(String idProyecto);
	
	String guardaDocumentoComprobantePago(String dactoPago,String codFact ,MultipartFile file, String tipoDocumento) throws Exception;
	
	Object GuardarRangoPago(RangoPago pago) throws Exception;
}
