package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_repre_legal", schema = "cuenta")
public class PersonaRepresentanteLegal implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_repre_legal")
	private Long idRepreLegal;
	
	@Column(name = "nombres", length = 50, nullable = false)
	private String nombres;
	
	@Column(name = "apellidos", length = 50, nullable = false)
	private String apellidos;
	
	@Column(name = "identificacion", length = 13, nullable = false)
	private String identificacion;
	
	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;
	
	@Column(name = "numero_celular", length = 10, nullable = false)
	private String numeroCelular;
	
	@Column(name = "cargo", length = 15, nullable = false)
	private String cargo;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_pais", nullable = false)
	private Pais pais;
	
	@Column(name = "telefono", length = 10, nullable = false)
	private String telefono;
	
	@Column(name = "direccion_domicilio", length = 200, nullable = false)
	private String direccionDomicilio;
	
	@Column(name = "fecha_inicio_actividad", nullable = false)
	private LocalDate fechaInicioActividad;
	
	@Column(name = "numero_domicilio", length = 10, nullable = false)
	private String numeroDomicilio;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
}
