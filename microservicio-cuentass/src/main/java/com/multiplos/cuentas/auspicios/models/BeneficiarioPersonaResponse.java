package com.multiplos.cuentas.auspicios.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioPersonaResponse {



	private String identificacion;
	private String tipoPersona;
	private String razonSocial;
	private int anioInicio;
	private String nombres;
	private String apellidos;
	private String nacionalidad;
	private Date fechaNacimiento;
	private String numeroCelular;
	
	
}
