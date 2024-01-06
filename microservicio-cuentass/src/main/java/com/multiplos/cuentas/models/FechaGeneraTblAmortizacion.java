package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_fecha_gen_tbl_Amort", schema = "negocio",
		indexes = {@Index(name="idx01_fechGen_idProy", columnList = "id_proyecto"),
					@Index(name="idx02_fechGen_fechGenera", columnList = "fecha_generacion"),
					@Index(name="idx03_fechGen_fechCreaci", columnList = "fecha_creacion")})
public class FechaGeneraTblAmortizacion implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column(name = "fecha_generacion", nullable = false)
	private LocalDate fechaGeneracion;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proyecto", unique = true, updatable = false, nullable = false)
	private Proyecto proyecto;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDate fechaCreacion;
	
	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
}
