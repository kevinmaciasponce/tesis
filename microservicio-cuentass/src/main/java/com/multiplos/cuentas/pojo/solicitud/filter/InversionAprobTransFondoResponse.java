package com.multiplos.cuentas.pojo.solicitud.filter;

import lombok.Data;

@Data
public class InversionAprobTransFondoResponse {

	private String codProyecto;
	private String nombreEmpresa;
	private String montoSolicitado;
	private int plazo;
	private String montoRecuadado;
	private String estado;
	
}
