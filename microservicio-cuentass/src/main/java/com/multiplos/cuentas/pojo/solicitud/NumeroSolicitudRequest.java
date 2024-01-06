package com.multiplos.cuentas.pojo.solicitud;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NumeroSolicitudRequest {

	@NotNull(message = "número Solicitud {NotNull}")
	private String numeroSolicitud;
}
