package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_titulos_deportivos", schema = "auspicios")
public class TitulosDeportivos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="beneficiario",updatable = false, nullable = true)
	@JsonIgnoreProperties(allowGetters = false)
	private Beneficiario beneficiario;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="disciplina",nullable = true)
	@JsonIgnoreProperties(allowGetters = false)
	private Disciplina disciplina;
	
	@Column(name="anio_titulo")
	private int anioTitulo;
	
	@Column(name="nombre_competencia",length = 50)
	private String nombreCompetencia;
	
	@Column(name="ranking_nacional",length = 50)
	private String rankingNacional;
	
	@Column(name="ranking_internacional",length = 50)
	private String rankingInternacional;
	
	
	@Column(name="otros",length = 50)
	private String otros;
	
	
	
	
}
