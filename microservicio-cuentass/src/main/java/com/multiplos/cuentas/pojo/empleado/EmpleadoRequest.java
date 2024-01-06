package com.multiplos.cuentas.pojo.empleado;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class EmpleadoRequest {

	@NotEmpty(message = "identificacion {NotEmpty}")
	@Size(min=3,max=13, message = "identificación debe tener entre 3 y 13 caracteres")
	private String idPersInterno;
	@NotEmpty(message = "nombres no debe estar vacio")
	@Size(min=3,max=50, message = "nombres debe tener entre 3 y 50 caracteres")
	@Pattern(regexp="[A-Za-z_ ]+",message = "nombres {Pattern.letras}")
	private String nombres;
	@NotEmpty(message = "apellidos no debe estar vacio")
	@Size(min=3,max=50, message = "apellidos debe tener entre 3 y 50 caracteres")
	@Pattern(regexp="[A-Za-z_ ]+",message = "apellidos {Pattern.letras}")
	private String apellidos;
	@NotEmpty(message = "iniciales no debe estar vacio")
	@Size(min=3,max=5, message = "iniciales debe tener entre 3 y 5 caracteres")
	@Pattern(regexp="[A-Za-z_]+",message = "iniciales {Pattern.letras}")
	private String iniciales;
	@NotEmpty(message = "usuario_creacion creacion no debe estar vacio")
	@Size(min=3,max=50, message = "usuario_creacion creacion debe tener entre 3 y 50 caracteres")
	private String usuarioCreacion;
}
