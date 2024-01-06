package com.multiplos.cuentas.pojo.persona;

import lombok.Data;

@Data
public class PersonaRequest {
	
	//@NotNull(message = "identificación {NotNull}")
	private String identificacion;
	private String tipoCliente;
}
