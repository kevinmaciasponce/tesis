package com.multiplos.cuentas.pojo.solicitud.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InversionEnProcesoResponse {
	
	private Long numeroSolicitud;
	private String nombreEmpresa;
	private String estado;
	private String codProyecto;

}
