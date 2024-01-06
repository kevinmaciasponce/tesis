package com.multiplos.cuentas.pojo.solicitud.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InversionPorConfirmarResponse {

	private Long numeroSolicitud;
	private String nombreEmpresa;
	private BigDecimal inversion;
	private int plazo;
	private String estado;
	private String observacion;
	private String codProyecto;

}
