package com.multiplos.cuentas.pojo.proyecto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.multiplos.cuentas.pojo.solicitud.NumeroSolicitudRequest;

import lombok.Data;

@Data
public class UpdatePYSRequest {
	@NotNull(message = "usuario {NotNull}")
	private String usuario;
	
	@NotNull(message = "codigo Proyecto {NotNull}")
	private String codigoProyecto;
	
	private String statusProyect;
	
	private String statusSol;
	
	private String searchSol;
	
	private List<Long> solicitudes;
	
	@NotNull(message = "Observacion de Proyecto {NotNull}")
	private  String observacionProyecto;
	
	
	private  String rutaComprobante;
	
}
