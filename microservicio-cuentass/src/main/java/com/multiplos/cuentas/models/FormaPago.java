package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_forma_pago", schema = "maestras")
public class FormaPago implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_forma_pago")
	private Long idFormaPago;
	
	@NotNull(message = "descripcion {NotNull}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóúÑñ ]+",message = "descripcion {Pattern.letras}")
	@Size(max=20 , message = "estado debe tener maximo 20 caracteres ")
	@Column(name = "descripcion",length = 20, unique = true, nullable = false)
	private String descripcion;
	
	@NotBlank(message = "estado no puede estar vacio")
	@Size(max=1 , message = "estado debe tener 1 caracter ")
	@Column(name = "estado",length = 1, nullable = false)
	private String estado;
	
	public FormaPago() {}
	public FormaPago(Long id) {this.idFormaPago=id;}
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
}
