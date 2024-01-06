package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuspicioResponse {
	private Long numeroAuspicio;
	private String identificacion;
	private BigDecimal montoSolicitado;
	private BigDecimal montoRecaudado;
	private String estado;
}
