package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_domicilios", schema = "cuenta")
public class PersonaDomicilio implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_domicilio")
	private Long idDomicilio;
	
	@OneToOne
	@JoinColumn(name = "id_pais", nullable = false)
	private Pais pais;

	@JsonIgnoreProperties(value = {"pais"}, allowSetters = true)
	@OneToOne
	@JoinColumn(name = "id_ciudad", nullable = false)
	private Ciudad ciudad;
	
	@Column(name = "direccion", length = 200, nullable = false)
	private String direccion;
	
	@Column(name = "numero_domicilio", length = 10, nullable = false)
	private String numeroDomicilio;
	
	@Column(name = "sector", length = 100, nullable = true)
	private String sector;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}

}
