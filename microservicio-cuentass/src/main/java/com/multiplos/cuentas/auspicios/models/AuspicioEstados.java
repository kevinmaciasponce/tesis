package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "mult_auspicios_estados", schema = "auspicios")

public class AuspicioEstados implements Serializable{

	private static final long serialVersionUID = 1L; 
	@Id
	@Column(name = "idEstado", length = 5, nullable = false)
	private String idEstado;
	
	@Column(name = "descripcion", unique = true, length = 50, nullable = false)
	private String descripcion;
	
	@Column(name = "estado",length = 1, nullable = false)
	private String estado;
	
	@Column(name = "detalle",length = 100, nullable = false)
	private String detalle;
	
	
	
	
}
