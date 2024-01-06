package com.multiplos.cuentas.pojo.cuenta;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UsuarioRequest {

	@NotNull(message = "correo {NotNull}")
	@Email(message = "{Email}")
	private String email;
	
	public UsuarioRequest() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
