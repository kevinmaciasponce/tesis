package com.multiplos.cuentas.pojo.amortizacion;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SimuladorRequest {

	private String identificacion;
	private String codigoProyecto;
	private BigDecimal inversion;
	private int plazo;
}
