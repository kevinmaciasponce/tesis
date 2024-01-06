package com.multiplos.cuentas.pojo.amortizacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DatosAmortizacion {
	
	private int periodo;
	private String numeroSolictud;
	private String codigoProyecto;
	private double inversion;
	private int plazo;
	private String usuario;
	private Long idTipoTabla;
	private BigDecimal porcentajeRendimiento;
	private LocalDate fechaEfectiva;
	
}
