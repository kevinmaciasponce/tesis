package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValoracionResponse {

	private Long id;
	private String calificacion;
	private LocalDate fechaCalificacion;
	private LocalDate fechaCaducidad;
	private BigDecimal presupuestoAprobado;
	private BigDecimal presupuestoRecaudado;
	private BigDecimal presupuestoRestante;
	private String ruta;
	private Boolean bianual;
	
	
	
}
