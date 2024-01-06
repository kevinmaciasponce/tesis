package com.multiplos.cuentas.pojo.empleado;

import lombok.Data;

@Data
public class PersonalDetalleResponse {

	private String nombres;
	private String apellidos;
	private Long idJefe;
	private String email;
	private String usuario;
	private Long idRol;
}
