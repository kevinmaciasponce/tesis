package com.multiplos.cuentas.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteCuotas {
	private String codProyecto;
	private String nombreProyecto;
	private int mes;
	private LocalDate fechaCuota;
	private String cuota;
	private BigDecimal totalCuota;
	private String estadoPago;
}
