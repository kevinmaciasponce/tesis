package com.multiplos.cuentas.pojo.transaccion;


import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class conciliaTransaccion {
	
	private LocalDate fecha;
	private BigDecimal monto;
}