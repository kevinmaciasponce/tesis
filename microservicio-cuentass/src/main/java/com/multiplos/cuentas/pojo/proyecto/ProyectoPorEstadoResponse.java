package com.multiplos.cuentas.pojo.proyecto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class ProyectoPorEstadoResponse {

	private String codProyecto;
	private String nombreEmpresa;
	private String inversionSolicitada;
	private int plazo;
	private String inversionRealizada;
	private BigDecimal porcentajeRecuadado;
	private String estado;
}
