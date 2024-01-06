package com.multiplos.cuentas.pojo.solicitud.filter;

import java.util.List;

import lombok.Data;

@Data
public class InversionSolPersonaResponse {

	private String totalInversion;
	private List<SolicitudPersonaResponse> solicitudes;
	
}
