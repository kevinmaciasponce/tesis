package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class AuspicioRequest {

	private Long numeroAuspicio;
	private String idBene;
	@NotNull(message = "presupuesto Solicitado {NotNull}")
	private BigDecimal presupuestoSolicitudo;
}
