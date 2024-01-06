package com.multiplos.cuentas.pojo.amortizacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmortizacionDetalleResponse {

	private String detalleCobro;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate fechaCobro;
	private BigDecimal rendimientoMensual;
	private BigDecimal saldoCapital;
	private BigDecimal cobrosCapital;
	private BigDecimal totalRecibir;
	
	private int cuota;
	private String estadoPago;
	private String rutaPago;
}
