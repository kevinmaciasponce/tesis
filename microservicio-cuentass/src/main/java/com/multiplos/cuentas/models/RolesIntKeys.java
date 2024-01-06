package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RolesIntKeys  implements Serializable {

	

	private static final long serialVersionUID = 1L;

	@NotNull(message = "rol  no puede estar vacio")
	//@Pattern(regexp="[0-9]+",message = "rol {Pattern.numeros}")
	@Column(name = "rol", nullable = false)
	private Long rolKey;

	@NotEmpty(message = "id cuenta no puede estar vacio")
	@Size(min=1,max=50,message = "id cuenta debe estar entre 1 y 50 caracteres")
	@Column(name = "cuenta",length = 50, nullable = true)
	private String cuentaInternaKey;
	

}
