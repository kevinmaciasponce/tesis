package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_parametros", schema = "parametrizacion",
		indexes = {@Index(name="idx01_param_parametro", columnList = "parametro, estado"),
				   @Index(name="idx02_param_valor", columnList = "valor, estado")})
public class Parametro implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_parametro")
	private Long idParametro;
	
	@Column(name = "parametro", length = 15, nullable = false)
	private String parametro;
	
	@Column(name = "cod_parametro", length = 20, nullable = false)
	private String codParametro;
	
	@Column(name = "valor", length = 100, nullable = false)
	private String valor;
	
	@Column(name = "descripcion", length = 200, nullable = false)
	private String descripcion;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;
	
	@Column(name = "usuario_modificacion", length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
}
