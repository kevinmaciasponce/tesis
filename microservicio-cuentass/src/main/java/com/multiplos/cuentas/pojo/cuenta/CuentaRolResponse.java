package com.multiplos.cuentas.pojo.cuenta;

import java.util.List;

import com.multiplos.cuentas.pojo.login.RolesResponse;

import lombok.Data;

@Data
public class CuentaRolResponse {

	private String usuario;
	private String identificacion;
	private String tipoCliente;
	private String tipoPersona;
	private String nombres;
	private String usuarioInterno;
	private String nick;
	private List<RolesResponse>roles;
	
	private String ruta;
}
