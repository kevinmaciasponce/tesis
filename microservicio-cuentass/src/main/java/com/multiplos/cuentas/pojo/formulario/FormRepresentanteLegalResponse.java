package com.multiplos.cuentas.pojo.formulario;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FormRepresentanteLegalResponse {
	private String nombres;
	private String apellidos;
	private String identificacion;
	private String email;
	private String numeroCelular;
	private String cargo;
	private PaisResponse pais;
	private String telefono;
	private String direccionDomicilio;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate fechaInicioActividad;
	private String numeroDomicilio;
}
