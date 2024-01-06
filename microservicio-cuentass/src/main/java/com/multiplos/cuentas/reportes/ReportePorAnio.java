package com.multiplos.cuentas.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePorAnio {
	
	private BigDecimal totalReporte;
	private int anio;
	private List<?> reportes;

//	public ReporteGeneral (BigDecimal totalReporte/*,String nombreInversionista*/) {
//		this.totalReporte=totalReporte;
//		//this.nombreInversionista= nombreInversionista;
//	}
	
}
