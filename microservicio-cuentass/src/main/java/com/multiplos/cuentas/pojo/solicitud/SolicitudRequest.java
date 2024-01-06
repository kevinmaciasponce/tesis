package com.multiplos.cuentas.pojo.solicitud;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SolicitudRequest {
	
	@NotNull(message = "identificación {NotNull}")
	private String identificacion;
	@NotNull(message = "codigo de proyecto {NotNull}")
	private String codigoProyecto;
	@NotNull(message = "inversión {NotNull}")
	private String inversion;
	
}
