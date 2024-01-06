package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mult_deport_categorias", schema = "maestrasAuspicios")
public class Categorias implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nombre ", length = 25, nullable = false)
	@NotBlank(message = "Nombre no debe estar vacio")
	@Size(min = 1, max = 25,message = "Nombre debe estar entre 1 y 25 caracteres")
	private String nombre ;
	
	@Column(name = "descripcion ", length = 50, nullable = false)
	@NotBlank(message = "descripcion no debe estar vacio")
	@Size(min = 1, max = 50,message = "descripcion debe estar entre 1 y 50 caracteres")
	private String descripcion ;
	
	@Column(name = "activo ",  nullable = true)
	private Boolean activo ;
	
	@PrePersist
	private void PrePersist() {
			this.activo=true;	
			}
}
