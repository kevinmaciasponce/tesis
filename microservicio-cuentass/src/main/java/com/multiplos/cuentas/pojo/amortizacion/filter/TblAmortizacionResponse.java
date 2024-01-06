package com.multiplos.cuentas.pojo.amortizacion.filter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblAmortizacionResponse {
	
	private Long id;
	private LocalDate fechaEfectiva;
	private LocalDateTime fechaGeneracion;
	private BigDecimal montoInversion;
	private int plazo;
	private BigDecimal rendimientoNeto;
	private BigDecimal rendimientoTotalInversion;
	private BigDecimal totalRecibir;
	
}
