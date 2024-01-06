package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RecompensaResponse {

	@NotNull
	private Long id;
	@NotNull
	private String Categoria;
	@NotNull
	private String porcentaje;
	@NotNull
	private String detalle;
}
