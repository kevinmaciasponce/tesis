package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InversionEnTransitoGerencia {

	private String codProyecto;
	private String nombreEmpresa;
	private BigDecimal inversionSolicitada;
	private int plazo;
	private String estado;
	
}
