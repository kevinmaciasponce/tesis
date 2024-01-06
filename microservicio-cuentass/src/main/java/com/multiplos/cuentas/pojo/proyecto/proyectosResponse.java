package com.multiplos.cuentas.pojo.proyecto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class proyectosResponse {

	private String idProyecto;
	private String nombreEmpresa;
	private BigDecimal montoSolicitado;
	private BigDecimal montoRecaudado;
	private int plazo;
	
	private int ronda;
	private LocalDate fechaInicioInversion;
	private LocalDate fechaLimiteInversion;
	private BigDecimal tasaEfectivaAnual;
	private String tipoInversion;
	private String destinoFinanciamiento;
	private String pagoInteres;
	private String pagoCapital;
	private String calificacion;
	private String estado;
	
	
	
}
