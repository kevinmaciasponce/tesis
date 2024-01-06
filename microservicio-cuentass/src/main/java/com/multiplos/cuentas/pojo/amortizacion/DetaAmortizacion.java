package com.multiplos.cuentas.pojo.amortizacion;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DetaAmortizacion {

	private String detalleCobro;
	private double rendimientoMensual;
	private double saldoCapital;
	private double cobrosCapital;
	private BigDecimal totalRecibir;
	private int cuota;
	private LocalDate fechaCobro;
	private String estadoPago;
	
}
