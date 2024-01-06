package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_tipo_tablas", schema = "maestras")
public class TipoTabla implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tipo_tabla")
	private Long idTipoTabla;

	@Column(name = "nombre", unique = true, length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "descripcion", length = 200, nullable = false)
	private String descripcion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	public TipoTabla() {}
	public TipoTabla(Long idTipo) {this.idTipoTabla= idTipo;}

}
