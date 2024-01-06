package com.multiplos.cuentas.pojo.persona;

import java.util.Date;

import lombok.Data;

@Data
public class PersonaResponse {
	
	private String identificacion;
	private String tipoCliente;
	private String tipoPersona;
	private String tipoIdentificacion;
	private String nacionalidad;
	private String nombres;
	private String apellidos;
	private Date fechaNacimiento;
	private String numeroCelular;
	private String razonSocial;
	private String nombreRepresentante;
	private String cargoRepresentante;
	private String emailRepresentante;
	private int anioInicioActividad;
	//datos de cuenta
    private String email;
	private String usuario;
	private String tipoContacto;
	private String usuarioContacto;
	private PersInfoAdicionalResponse datosFormulario;

}
