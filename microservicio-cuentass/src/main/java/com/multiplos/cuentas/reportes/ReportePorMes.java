package com.multiplos.cuentas.reportes;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportePorMes {
	private String mes;
	private Boolean existe;
	private String info;
	private BigDecimal totalMes;
	private List<?> reporteFechaProyecto;
	
}
