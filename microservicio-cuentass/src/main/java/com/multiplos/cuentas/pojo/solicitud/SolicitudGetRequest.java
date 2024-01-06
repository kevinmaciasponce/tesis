package com.multiplos.cuentas.pojo.solicitud;

import lombok.Data;

@Data
public class SolicitudGetRequest {

	private String identificacion;
	private String codigoProyecto;
	private String estado;
	
}
