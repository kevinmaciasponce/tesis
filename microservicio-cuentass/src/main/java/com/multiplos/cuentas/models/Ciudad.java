package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_ciudades", schema = "maestras")
public class Ciudad implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ciudad")
	private Long idCiudad;
	
	@NotEmpty(message = "ciudad no puede estar vacio")
	@Size(min=1,max=100, message = "ciudad debe estar entre 1 y 100 caracteres")
	@Pattern(regexp="[A-Za-zÁÉÍÓÚáéíóú .]+",message = "debe ingresar solo letras")
	@Column(name = "ciudad", length = 100, nullable = false)
	private String ciudad;
	
	@OneToOne()
	@NotNull(message = "pais no puede estar vacio")
	@JoinColumn(name = "id_pais", nullable = false)
	private Pais pais;
	
	@NotEmpty(message = "estado no puede estar vacio")
	@Size(min=1,max=100, message = "estado debe estar tener 1 caracter")
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
}
