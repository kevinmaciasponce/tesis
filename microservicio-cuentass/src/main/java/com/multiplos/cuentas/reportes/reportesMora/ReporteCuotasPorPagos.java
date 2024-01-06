package com.multiplos.cuentas.reportes.reportesMora;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteCuotasPorPagos {

	private LocalDate fechaCobro;
	private String numCuota;
	private LocalDate fechaPromedioPago;
	private int diasAtraso;
	
	public ReporteCuotasPorPagos(LocalDate fechaCobro,String numCuota) {
		this.fechaCobro= fechaCobro;
		this.numCuota= numCuota;
	}
}
