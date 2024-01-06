package com.multiplos.cuentas.auspicios.models;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TitulosDepostivosResponse {

	@NotNull
	private Long id;
	@NotNull
	private int anioTitulo;
	@NotNull
	private String nombreCompetencia;
	@NotNull
	private String rankingNacional;
	@NotNull
	private String rankingInternacional;
	@NotNull
	private String otros;
}
