package com.multiplos.cuentas.pojo.solicitud;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateSolicitudRequest {
	
	@NotNull(message = "usuario {NotNull}")
	private String usuario;
	@NotNull(message = "numero solicitud {NotNull}")
	private String numeroSolicitud;
	@NotNull(message = "inversión {NotNull}")
	private String inversion;
	
}
