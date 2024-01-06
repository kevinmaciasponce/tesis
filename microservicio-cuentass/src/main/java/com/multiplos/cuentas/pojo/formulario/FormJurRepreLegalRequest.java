package com.multiplos.cuentas.pojo.formulario;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FormJurRepreLegalRequest {
	
	@Size(min = 4, max = 50, message = "nombres {Size.nombreApellido}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "nombres {Pattern.letras}")
	private String nombres;
	@Size(min = 4, max = 50, message = "apellidos {Size.nombreApellido}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "apellidos {Pattern.letras}")
	private String apellidos;
	@NotNull(message = "identificación {NotNull}")
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.identificacion}")
	private String identificacion;
	@NotNull(message = "Correo {NotNull}")
	@Email(message = "{Email}")
	private String email;
	@NotNull(message = "celular {NotNull}")
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.CuentaRequest.numeroCelular}")
	private String celular;
	@NotNull(message = "Cargo {NotNull}")
	@Pattern(regexp="[A-Za-z_]+",message = "Cargo {Pattern.letras}")
	private String cargo;
	@NotNull(message = "País {NotNull}")
	private Long pais;
	@NotNull(message = "telefono {NotNull}")
	private String telefono;
	@NotNull(message = "direccion Domicilio {NotNull}")
	private String direccionDomicilio;
	@NotNull(message = "fecha Inicio Actividad {NotNull}")
	private LocalDate fechaInicioActividad;
	@NotNull(message = "numero Domicilio {NotNull}")
	private String numeroDomicilio;
}
