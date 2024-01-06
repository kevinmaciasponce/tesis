package com.multiplos.cuentas.auspicios.models;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.multiplos.cuentas.models.Pais;

import lombok.Data;
@Data
public class TorneosAllow {
	
	@NotNull(message = "numeroAuspicio no debe ser nulo")
	private Long numeroAuspicio;
	@NotEmpty(message = "identificacion no debe ser nulo")
	 private String identificacion;
	
	@Valid
	@NotNull(message = "torneos no debe ser nulo")
	private List<AuspicioTorneo>torneos;
	
}
