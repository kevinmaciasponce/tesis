package com.multiplos.cuentas.pojo.formulario;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FormJurEstFinanRequest {
	
	@NotNull
	private BigDecimal ingresoAnual;
	@NotNull
	private BigDecimal egresoAnual;
	@NotNull
	private BigDecimal totalActivo;
	@NotNull
	private BigDecimal totalPasivo;
	@NotNull
	private BigDecimal totalPatrimonio;
}
