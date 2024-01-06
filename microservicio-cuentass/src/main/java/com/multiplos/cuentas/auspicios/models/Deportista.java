package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.multiplos.cuentas.models.Persona;

import lombok.Data;

@Data
//@Entity
//@Table(name = "mult_deportista", schema = "auspicios")
public class Deportista implements Serializable {

	private static final long serialVersionUID = 1L; 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona", updatable = false, nullable = false, unique=true)
	private Persona persona;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "disciplina ", updatable = false, nullable = false)
	private Disciplina disciplina;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="categoria", updatable = false, nullable = false)
	private Categorias categoria;	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="modalidad", updatable = false, nullable = false)
	private Modalidad modalidad;
	
	@Column(name ="genero",length = 1, updatable = false, nullable = false)
	private String genero;	
	
	@Column(name ="titulo",length = 50, updatable = false, nullable = false)
	private String titulo;	
	
	
}
