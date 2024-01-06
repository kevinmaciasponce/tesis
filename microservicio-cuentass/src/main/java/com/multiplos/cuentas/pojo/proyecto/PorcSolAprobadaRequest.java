package com.multiplos.cuentas.pojo.proyecto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.multiplos.cuentas.pojo.solicitud.NumeroSolicitudRequest;

import lombok.Data;

@Data
public class PorcSolAprobadaRequest {

	@NotNull(message = "usuario {NotNull}")
	private String usuario;
	
	@NotNull(message = "codigo Proyecto {NotNull}")
	private String codigoProyecto;
	
	@NotNull(message = "monto solicitado {NotNull}")
	private String montoSolicitado;
	
	@NotNull(message = "monto aprobado {NotNull}")
	private String montoAprobado;
	
	@NotNull(message = "observacion {NotNull}")
	private String observacion;
	
	@NotNull(message = "solicitudes {NotNull}")
	private List<NumeroSolicitudRequest> solicitudes;
}
