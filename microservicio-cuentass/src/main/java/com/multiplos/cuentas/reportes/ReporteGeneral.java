package com.multiplos.cuentas.reportes;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ReporteGeneral {
	private BigDecimal totalReporte;
	private List<?> reportes;
}
