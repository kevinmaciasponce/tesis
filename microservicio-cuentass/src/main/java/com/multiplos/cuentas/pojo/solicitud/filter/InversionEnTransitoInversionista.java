package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InversionEnTransitoInversionista {

	private Long numeroSolicitud;
	private String nombreEmpresa;
	private BigDecimal inversion;
	private int plazo;
	private String estado;
	private String codProyecto;
	private BigDecimal montoPago;
	private BigDecimal montoSolicitado;
	
}
