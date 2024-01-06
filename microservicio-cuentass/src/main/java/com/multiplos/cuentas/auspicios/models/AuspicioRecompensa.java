package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mult_auspicios_recompesas", schema = "auspicios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuspicioRecompensa implements Serializable{

	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="auspicio",nullable = false)
	@JsonIgnoreProperties(allowGetters   =false)
	private Auspicio auspicio;

	@NotEmpty(message = "categoria {NotNull}")
	@Column(name ="categoria",length = 20)
	@Size(min=0, max=20 , message = "categoria debe tener entre 0 y 20 caracteres")
	private String categoria;

	@NotEmpty(message = "porcentaje {NotNull}")
	@Column(name = "porcentaje",length = 20,nullable = false)
	@Size(min=0, max=20 , message = "porcentaje debe tener entre 0 y 20 caracteres")
	private String porcentaje;
	
	@NotEmpty(message = "detalle {NotNull}")
	@Column(name="detalle", length = 200)
	@Size(min=0, max=200 , message = "detalle debe tener entre 0 y 200 caracteres")
	private String detalle;
	
}
