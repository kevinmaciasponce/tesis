package com.multiplos.cuentas.pojo.plantilla.solicitud;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatoSolicitud {

	private String identificacion;
	private String inversionista;
	private Long numeroSolicitud;
	private String empresa;
	private String codigoProyecto;
	private BigDecimal montoInversion;
	private int plazo;
	private BigDecimal rentabilidad;
	private String email;
	
}
