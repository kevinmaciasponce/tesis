package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mult_nacionalidades", schema = "maestras",
		indexes = {@Index(name="idx01_naciona_id", columnList = "id_nacionalidad"),
				   @Index(name="idx02_naciona_iso", columnList = "iso, estado")})
public class Pais implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_nacionalidad")
	private Long idNacionalidad;
	
	@NotNull(message = "pais no puede estar vacio")
	@Size(min = 3, max = 100,message = "pais debe estar entre 3 y 100 caracteres")
	@Pattern(regexp="[A-Za-zÁÉÍÓÚáéíóú ]+",message = "debe ingresar solo letras")
	@Column(name = "pais", length = 100, nullable = false)
	private String pais;
	
	@NotNull(message = "gentilicio no puede estar vacio")
	@Size(min = 3, max = 100,message = "gentilicio debe estar entre 3 y 100 caracteres")
	@Pattern(regexp="[A-Za-z_ ]+",message = "gentilicio debe ingresar solo letras")
	@Column(name = "gentilicio", length = 100, nullable = false)
	private String gentilicio;
	
	@NotNull(message = "iso no puede estar vacio")
	@Size(max = 3,message = "iso debe estar tener maximo 2 caracteres")
	@Pattern(regexp="[A-Za-z_ ]+",message = "iso debe ingresar solo letras")
	@Column(name = "iso", length = 3, nullable = false)
	private String iso;
	
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
