package com.multiplos.cuentas.reportes;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ReportePorInvestors {

	private String nombre;
	private String identificacion;
	private BigDecimal totalInvestor;
	private List<?> reportePorFechas;
	
	
}
