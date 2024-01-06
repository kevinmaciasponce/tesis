package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_indicadores", schema = "promotor")
public class Indicador implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_indicador")
	private Long idIndicador;
	
	@Type(type = "text")
	@Column(name = "solvencia", nullable = false)
	private String solvencia;
	
	@Column(name = "porcentaje_solvencia", nullable = false)
	private double porcentajeSolvencia;
	
	@Type(type = "text")
	@Column(name = "liquidez", nullable = false)
	private String liquidez;
	
	@Column(name = "porcentaje_liquidez", nullable = false)
	private double porcentajeLiquidez;
	
	@Type(type = "text")
	@Column(name = "retorno_capital", nullable = false)
	private String retornoCapital;
	
	@Column(name = "porcentaje_retorno_capital", nullable = false)
	private double porcentajeRetornoCapital;
	
	@Type(type = "text")
	@Column(name = "garantia", nullable = false)
	private String garantia;
	
	@Column(name = "porcentaje_garantia", nullable = false)
	private double porcentajeGarantia;
	
	@Column(name = "anio", nullable = false)
	private Long anio;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

	
}
