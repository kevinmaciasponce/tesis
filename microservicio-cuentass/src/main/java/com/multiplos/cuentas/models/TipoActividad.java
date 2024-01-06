package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "mult_tipo_actividades", schema = "promotor")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoActividad implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_actividad")
	private Long idActividad;

	@Column(name = "nombre", unique = true, length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;

}
