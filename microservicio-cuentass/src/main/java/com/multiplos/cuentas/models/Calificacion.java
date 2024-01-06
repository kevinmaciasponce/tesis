package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@Table(name = "mult_tipo_calificaciones", schema = "promotor")
public class Calificacion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tipo_calificacion")
	private Long idTipoCalificacion;

	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	public Calificacion (Long idTipoCalificacion) {
		this.idTipoCalificacion=idTipoCalificacion;
	}

}
