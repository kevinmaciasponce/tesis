package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InversionEnTransitoGerenciaResponse {

	private String codProyecto;
	private String nombreEmpresa;
	private String inversionSolicitada;
	private int plazo;
	private String inversionRealizada;
	private BigDecimal porcentajeRecuadado;
	private String estado;
	
}
