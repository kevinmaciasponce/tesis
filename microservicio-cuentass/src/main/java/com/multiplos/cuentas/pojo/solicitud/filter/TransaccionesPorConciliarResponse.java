package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransaccionesPorConciliarResponse {

	private Long numeroSolicitud;
	private String identificacion;
	private String nombreEmpresa;
	private BigDecimal inversion;
	private int plazo;
	private String estado;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate fechaPago;
	private String observacion;
	private String codProyecto;
	private String numeroComprobante;
	private BigDecimal montoPago;
}
