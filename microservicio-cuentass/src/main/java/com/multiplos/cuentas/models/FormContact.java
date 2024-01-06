package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
@Data
@Entity
@Table(name = "mult_formContact", schema = "footer")
public class FormContact implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull (message = "nombres {NotNull}")
	@Size(min = 4, max = 50, message = "nombres {Size.nombreApellido}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "nombres {Pattern.letras}")
	@Column(name = "nombres", length = 100, nullable = false)
	private String nombres;
	
	@NotNull(message = "identificacion {NotNull}")
	@Size(min = 10, max = 13, message = "{Size.identificacion}")
	@Pattern(regexp="[0-9]+",message = "identificación {Pattern.numeros}")
	@Column(name = "identificacion", length = 13, nullable = false)
	private String identificacion;
	
	@NotNull (message ="correo {NotNull}")
	@Email (message ="{email}")
	@Column(name = "email", length = 100, nullable = false)
	private String email;
	
	@NotNull (message = "celular {NotNull}")
	@Pattern(regexp="[0-9]+",message = "telefono {Pattern.numeros}")
	@Column(name = "telefono", length = 10, nullable = false)
	private String telefono;
	
	@NotNull (message = "Ciudad {NotNull}")
	@Size(min = 4, max = 50, message = "ciudad {Size.ciudad}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "ciudad {Pattern.letras}")
	@Column(name = "ciudad", length = 50, nullable = false)
	private String ciudad;
	
	@NotNull(message = "motivo {NotNull}")
	@Size(min = 4, max = 50, message = "motivo {Size.motivo}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "nombres {Pattern.letras}")
	@Column(name = "motivo", length = 10, nullable = false)
	private String motivo;
	
	@NotNull (message = "mensaje {NotNull}")
	@Size(min = 4, max = 200, message = "mensaje {Size.mensaje}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "mensaje {Pattern.letras}")
	@Column(name = "mensaje", length = 200, nullable = false)
	private String mensaje;
	
	@OneToOne
	@JoinColumn(name = "estadoActual", nullable = false)
	private TipoEstado estadoActual;
	
	
	@OneToOne
	@JoinColumn(name = "estadoAnterior", nullable = true)
	private TipoEstado estadoAnterior;
	
	
}
