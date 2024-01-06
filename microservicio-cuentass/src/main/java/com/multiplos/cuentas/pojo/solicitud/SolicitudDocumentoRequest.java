package com.multiplos.cuentas.pojo.solicitud;

import lombok.Data;

@Data
public class SolicitudDocumentoRequest {

	private Long numeroSolicitud;
	private String identificacion;
	private String usuario;
}
