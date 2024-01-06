package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_bancos", schema = "maestras")
public class Banco implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_banco")
	private Long idBanco;
	
	@NotEmpty(message = "Nombre no puede estar vacio")
	@Size(min=1,max=100, message = "Nombre debe estar entre 1 y 100 caracteres")
	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@NotEmpty(message = "estado no puede estar vacio")
	@Size(min=1,max=100, message = "estado debe estar tener 1 caracter")
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	@PrePersist
	public void prePersist() {
		if(this.estado==null) {
			this.estado = "A";
		}
		
	}
}
