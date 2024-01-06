package com.multiplos.cuentas.pojo.formulario;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


import lombok.Data;

@Data
public class FormTipoCuentaRequest {

	@NotNull(message = "titular de la cuenta {NotNull}")
	private String titularCuenta;
	@NotNull(message = "Nombre del Banco {NotNull}")
	private Long bancoCuenta;
	@NotNull(message = "Tipo de cuenta {NotNull}")
	private String tipoCuenta;
	@NotNull(message = "Número de cuenta {NotNull}")
	private String numeroCuenta;
}
