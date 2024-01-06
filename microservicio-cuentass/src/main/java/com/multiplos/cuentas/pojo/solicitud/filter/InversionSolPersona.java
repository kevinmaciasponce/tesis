package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InversionSolPersona {

	private Long numeroSolicitud;
	private BigDecimal montoInversion;
	private String tipoPersona;
	private String nombres;
	private String apellidos;
	private String razonSocial;
	private String identificacion;

}
