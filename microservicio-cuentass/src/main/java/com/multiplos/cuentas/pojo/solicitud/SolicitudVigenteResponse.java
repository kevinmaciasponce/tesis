package com.multiplos.cuentas.pojo.solicitud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SolicitudVigenteResponse {
	private String codProyecto;
	private String nomEmpresa;
	private Long numSolicitud;
	private int plazo;
//	private String porcRecaudado;
	private LocalDateTime fechaVigencia;
	private BigDecimal monto;
	private int cuota;
	private String estadoPago;
	private LocalDate fechaProxima;
	private Long idDetAmortizacion;
	private BigDecimal cuotaPago;
	private String identificacion;
	
}
