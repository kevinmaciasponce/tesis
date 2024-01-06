package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multiplos.cuentas.models.DetalleAmortizacion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class inversionResponse {
	private Long numeroSolicitud;
	private String identificacion;
	private String nombreEmpresa;
	private BigDecimal inversion;
	private int plazo;
	private String estado;
	private String codProyecto;
	private BigDecimal montoPago;
	private BigDecimal montoProyecto;
}
