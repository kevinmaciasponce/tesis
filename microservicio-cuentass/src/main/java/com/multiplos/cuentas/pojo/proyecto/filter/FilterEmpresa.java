package com.multiplos.cuentas.pojo.proyecto.filter;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterEmpresa {
	private String codProyecto;
	private String nombreEmpresa;
	private BigDecimal montoSolicitado; 
}
