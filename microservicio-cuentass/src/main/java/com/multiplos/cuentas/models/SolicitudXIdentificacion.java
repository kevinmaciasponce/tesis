package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_sol_x_identifiacion", schema = "historicas",
		indexes = {@Index(name="idx01_sol", columnList = "solicitud")
		})

public class SolicitudXIdentificacion implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	
	@JoinColumn(name = "solicitud", nullable = false, updatable = false)
	private Solicitud solicitud;
	
	@JoinColumn(name = "documento", nullable = false, updatable = false)
	private PersonaDocumento documento;
	
	@JoinColumn(name = "usuario_creacion", nullable = false, updatable = false)
	private Cuenta usuarioCreacion;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDate fechaCreacion;
	
	@PrePersist
	public void prePersist() {
		this.fechaCreacion = LocalDate.now();
	}
	
}
