package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_proyectos_rutas", schema = "promotor")
public class ProyectoRuta implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_proyecto_ruta")
	private Long idProyectoRuta;
	
	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;
	
	@Column(name = "ruta", length = 200, nullable = false)
	private String ruta;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@JsonIgnoreProperties(value = {"proyectoRutas"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proyecto", nullable = false)
	private Proyecto proyecto;
		
}
