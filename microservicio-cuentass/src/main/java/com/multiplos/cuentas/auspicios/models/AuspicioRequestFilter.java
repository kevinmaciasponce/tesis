package com.multiplos.cuentas.auspicios.models;

import lombok.Data;

@Data
public class AuspicioRequestFilter {

	
	private String identificacion;
	private Long numeroAuspicio;
	private Boolean Activos;
}
