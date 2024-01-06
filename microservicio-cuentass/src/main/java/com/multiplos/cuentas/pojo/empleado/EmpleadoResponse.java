package com.multiplos.cuentas.pojo.empleado;

import lombok.Data;

@Data
public class EmpleadoResponse {
	
	private String idEmpleado;
	private String usuario;
	private String nombres;
	private String iniciales;
	private Long idJefe;
	
}
