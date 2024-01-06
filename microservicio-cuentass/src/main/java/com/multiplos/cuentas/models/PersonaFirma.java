package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_firmas", schema = "cuenta")
public class PersonaFirma implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_firma")
	private Long idFirma;
	
	@Column(name = "nombres_completos", length = 100, nullable = false)
	private String nombresCompletos;
	
	@Column(name = "identificacion", length = 13, nullable = false)
	private String identificacion;
	
	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
}
