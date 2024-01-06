package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_proyectos_cuentas", schema = "promotor", 
		indexes = {@Index(name="idx01_proycuenta_idproyecto", columnList = "id_proyecto")}
		)
public class ProyectoCuenta implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_proyecto_cuenta")
	private Long idProyectoCuenta;
	
	@OneToOne
	@JoinColumn(name = "id_banco", nullable = false)
	private Banco banco;
	
	@Column(name = "tipo_cuenta", length = 10, nullable = false)
	private String tipoCuenta;
	
	@Column(name = "numero_cuenta", length = 20, nullable = false)
	private String numeroCuenta;

	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@ManyToOne
	@JoinColumn(name = "id_proyecto", nullable = false)
	private Proyecto proyecto;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;

	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;

	@Column(name = "usuario_modificacion", length = 50, nullable = true)
	private String usuarioModificacion;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
	
}
