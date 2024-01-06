package com.multiplos.cuentas.pojo.formulario;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FormDomicilioRequest {
	
	@NotNull(message = "pais no null")
	private Long pais;
	@NotNull(message = "ciudad no null")
	private Long ciudad;
	@NotNull(message = "ciudad no null")
	@Size(min = 4, max = 200)
    private String direccion;
	@NotNull(message = "numeroDomicilio no null")
	@Size(min = 1, max = 10)
	private String numeroDomicilio;
	@NotNull(message = "sector no null")
    private String sector;

}
