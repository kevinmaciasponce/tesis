package com.multiplos.cuentas.pojo.solicitud.filter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class InversionHistorialEstado {

	private String estadoActual;
	private String estadoAnterior;
	private String usuario;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDateTime fecha;
	
}
