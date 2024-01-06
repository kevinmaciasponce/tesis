package com.multiplos.cuentas.pojo.proyecto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicadorResponse {

	private Long idIndicador;
	private String solvencia;
	private Double porcentajeSolvencia;
	private String liquidez;
	private Double porcentajeLiquidez;
	private String retornoCapital;
	private Double porcentajeRetornoCapital;
	private String garantia;
	private Double porcentajeGarantia;
	private Long anio;
	
}
