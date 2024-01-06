package com.multiplos.cuentas.pojo.amortizacion;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AmortizacionRequest {
	
	//@NotNull(message = "número Solicitud {NotNull}")
	private Long numeroSolictud;
	
	@NotNull(message = "código Proyecto {NotNull}")
	private String codigoProyecto;
	
	@NotNull(message = "monto inversión {NotNull}")
	private double inversion;

	private int periodoPago;
	
	private String usuario;
	private String identificacion;
	private Long idTipoTabla;
}
