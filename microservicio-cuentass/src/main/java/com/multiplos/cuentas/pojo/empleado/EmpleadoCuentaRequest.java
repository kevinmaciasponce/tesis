package com.multiplos.cuentas.pojo.empleado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.multiplos.cuentas.models.PersonaInterna;
import com.multiplos.cuentas.models.RolesInternos;

import lombok.Data;

@Data

public class EmpleadoCuentaRequest {

	
	private String idCuenta;
	
	@NotBlank(message = "email no puede estar vacio")
	@Size(min = 8,max = 100, message = "Email debe tener entre 8 y 100 caracteres")
	@Email(message = "Email no valido")
	private String email;
	
	@NotNull(message = "clave no puede ser nulo")
	@Size(min = 8,max = 100,  message = "clave debe tener entre 8 y 100 caracteres")
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\\d$@$.,#/<>;:*!%?&].{6,12}$", message="{clave}")
	private String clave;
	
	@NotEmpty(message = "activa no puede estar vacio")
	@Size(max = 1, message = "activa debe tener 1 caracterer")
	private String cuentaActiva;
	
	@NotEmpty(message = "usuario_creacion no puede estar vacio")
	@Size(max = 50, message = "usuario_creacion debe tener entre 50 caracteres")
	private String usuarioCreacion;
	
	
	@NotEmpty(message = "personalInterno no puede estar vacio")
	@Size(max = 50, message = "personalInterno debe tener entre 50 caracteres")
	private String personalInterno;
	
}
