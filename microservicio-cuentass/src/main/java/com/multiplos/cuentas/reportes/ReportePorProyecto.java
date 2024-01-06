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
public class ReportePorProyecto {
	private String codProyect;
	private String nombreEmpresa;
	private BigDecimal totalProyecto;
	private List<?> listaCuotas;
	
	public ReportePorProyecto(String codProyecto,String nombreEmpresa) {
		this.codProyect=codProyecto;
		this.nombreEmpresa=nombreEmpresa;
	}
}
