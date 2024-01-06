package com.multiplos.cuentas.pojo.login;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class RequestLogin {
	
	@NotNull(message = "usuario {NotNull}")
	@Email(message = "{Email}")
	private String usuario;
	
	@NotNull(message = "Contraseña {NotNull}")
	private String clave;
	
	public RequestLogin() {
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getClave() {
		return clave;
	}
	
	public void setClave(String clave) {
		this.clave = clave;
	}
		

}
