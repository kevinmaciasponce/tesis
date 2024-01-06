package com.multiplos.cuentas.auspicios.models;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RecompensasAllow {

	
	private Long numeroAuspicio;
	@NotEmpty(message= "identificacion no puede estar vacio")
	private String identificacion;
	@Valid
	@NotNull(message = "recompensas no debe ser nulo")
	private List<AuspicioRecompensa>recompensas;
	
	
}
