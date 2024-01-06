package com.multiplos.cuentas.pojo.plantilla.solicitud;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DatoProyecto {

	private String codigoProyecto;
	private String nombreEmpresa;
	private BigDecimal porcentaje;
	private String montoRecaudado;
}
