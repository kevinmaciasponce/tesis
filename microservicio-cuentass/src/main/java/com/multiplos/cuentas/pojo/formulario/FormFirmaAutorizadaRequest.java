package com.multiplos.cuentas.pojo.formulario;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FormFirmaAutorizadaRequest {

	@NotNull(message = "Nombres de firma autorizada {NotNull}")
	@Size(min = 4, max = 100, message = "Nombres de firma autorizada {Size.longitud}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "debe ingresar solo letras")
	private String nombresCompletos;
	@NotNull(message = "Identificación de firma autorizada {NotNull}")
	@Pattern(regexp="[0-9]+",message = "Identificación de firma autorizada {Pattern.numeros}")
	private String identificacion;
	@NotNull(message = "Correo de firma autorizada {NotNull}")
	@Email(message = "{Email}")
	private String email;
}
