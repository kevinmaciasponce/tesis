package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InversionEnTransitoResponse {

	private String numeroSolicitud;
	private String codProyecto;
	private String nombreEmpresa;
	private String montoInversion;
	private int plazo;
	private String montoPago;
	private BigDecimal Recaudado;
	private String estado;
	
}
