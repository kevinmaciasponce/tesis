package com.multiplos.cuentas.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "mult_rango_pagos", schema = "promotor")
public class RangoPago {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "min",precision = 12,scale = 2,nullable = false)
	private BigDecimal min;
	
	@Column(name = "max",precision = 12,scale = 2,nullable = false)
	private BigDecimal max;
	
	@Column(name = "valor",precision = 12,scale = 2,nullable = false)
	private BigDecimal valor;
	
	@Column(name = "activo",nullable = false)
	private Boolean activo;
	
	@Column(name = "usuario_creacion",nullable = false)
	private String usuariocreacion;
}
