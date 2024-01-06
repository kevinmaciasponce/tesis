package com.multiplos.cuentas.models;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_tipo_Solicitud", schema = "maestras")
public class TipoSolicitud implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "descripcion",length = 20, unique = true, nullable = false)
	private String descripcion;
	
	@Column(name = "estado",length = 1, nullable = false)
	private String estado;
	
	public TipoSolicitud() {}
	public TipoSolicitud(Long id) {this.id= id; }
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
	
}
