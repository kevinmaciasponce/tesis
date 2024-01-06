package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_tipo_estados", schema = "inversion")
public class TipoEstado implements Serializable{
	
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
	
	public TipoEstado (String id) {this.idEstado=id;}
	public  TipoEstado () {}
}
