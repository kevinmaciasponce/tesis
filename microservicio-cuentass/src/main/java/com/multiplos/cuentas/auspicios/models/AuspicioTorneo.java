package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;
import java.time.LocalDate;

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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.multiplos.cuentas.models.Pais;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "mult_auspicios_torneos", schema = "auspicios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AuspicioTorneo implements Serializable{
	
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="auspicio",nullable = false,updatable = false)
	private Auspicio auspicio;
	
	@NotEmpty(message = "nombre del torneo no debe ser nulo")
	@Column(name="nombre_torneo",length = 50)
	@Size(message = "nombre de torneo debe tener entre 0 y 50 caracteres")
	private String nombreTorneo;

	@OneToOne
	@JoinColumn(name="pais")
	private Pais pais;
	
	@Column(name="fecha")
	private LocalDate fecha;
	
	
	
}
