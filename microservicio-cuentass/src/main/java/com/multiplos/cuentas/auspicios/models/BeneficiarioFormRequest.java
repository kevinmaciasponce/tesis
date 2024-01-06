package com.multiplos.cuentas.auspicios.models;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BeneficiarioFormRequest {
	
	
	//PERSONA BENEFICIARIA
	@NotNull(message = "tipo cliente {NotNull}")
	@Size(min=3, max=3 , message = "Tipo persona puede ser NAT O JUR")
	private String tipoCliente;
	
	@Size(min=0, max=100 ,  message = "Razon social debe tener entre 0 y 100 caracteres")
	@NotNull(message = "razon social {NotNull}")
	private String razonSocial;
	
	@NotNull(message = "anio Inicio Actividad {NotNull}")
	private Integer annioInicioAct;
	
	@Size(min=0, max=50 , message = "nombres debe tener entre 0 y 50 caracteres")
	@NotNull(message = "nombres {NotNull}")
	private String nombres;
	
	@NotNull(message = "apellidos {NotNull}")
	private String apellidos;
	
	@Size(min=10, max=14, message = "celular {Size.celular}")
	@NotEmpty(message = "celular {NotNull}")
	private String celular;
	
	@NotEmpty(message = "nacionalidad {NotNull}")
	private String nacionalidad;
	
	@Size(min=10, max=13, message = "{Size.identificacion}")
	@NotEmpty(message = "identificacion {NotNull}")
	private String identificacion;
	
	@Email(message = "correo no valido")
	@NotEmpty(message = "identificacion {NotNull}")
	private String correo;
	
	@JsonFormat(pattern = "yyyy-mm-dd")
	@NotNull(message = "fecha de nacimiento {NotNull}")
	private Date fechaNacimiento;
	
	@NotNull(message = "banco {NotNull}")
	private Long idBanco;
	@NotEmpty(message = "tipo cuenta {NotNull}")
	@Size(min=4, max=13, message = "tipo cuenta debe tener entre 4 y 13 caracteres")
	private String tipoCuenta;
	@Size(min=4, max=50, message = "numero de cuenta debe tener entre 4 y 50 caracteres")
	@NotEmpty(message = "numero cuenta {NotNull}")
	private String numeroCuenta;
	
	
	
	//DATOS DEPORTISTA
	@NotEmpty(message = "representante {NotNull}")
	private String idRepresentante;
	
	@Size( max = 700, message = "perfil deportivo debe tener entre 0 y 700 caracteres")
	@NotBlank(message = "perfil {NotNull}")
	private String perfil;
	@Size( max = 50, message = "titulo actual debe tener entre 0 y 50 caracteres")
	@NotBlank(message = "titulo actual {NotNull}")
	private String tituloActual;
	
	@NotNull(message = "disciplina {NotNull}")
	private Long disciplina;
	@NotNull(message = "categoria {NotNull}")
	private Long categoria;
	@NotNull(message = "modalidad {NotNull}")
	private Long modalidad;
	
	
	
}
