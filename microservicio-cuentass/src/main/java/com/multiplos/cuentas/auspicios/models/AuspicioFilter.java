package com.multiplos.cuentas.auspicios.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AuspicioFilter {

	//@NotNull(message = "numero de auspicio no puede estar vacio")
	private Long id;
	
	@NotNull(message = "nombre o apellido no puede estar vacio")
	private String nomApe="";
	
	@NotNull(message = "Identificacion no puede estar vacio")
	private String identificacion="";
	
	@NotNull(message = "Estado no puede estar vacio;")
	private String estado="";
	
	//@NotNull(message = "disciplina no puede estar vacio")	
	private Long disciplina;

	
}
