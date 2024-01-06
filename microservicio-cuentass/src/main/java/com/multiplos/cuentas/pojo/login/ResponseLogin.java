package com.multiplos.cuentas.pojo.login;

import java.util.List;

import com.multiplos.cuentas.models.Roles;

import lombok.Data;

@Data
public class ResponseLogin {

	private String mensaje;
	private String token;
	
	private String idCuenta;
	private String usuarioInterno;
	private String nombres;
	private String apellidos;
	private String tipoCliente;
	private String tipoPersona;
	private String identificacion;
	
	private List<RolesResponse>roles;

}
