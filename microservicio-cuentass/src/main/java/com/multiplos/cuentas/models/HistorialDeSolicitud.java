package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_hist_solicitud", schema = "historicas")
public class HistorialDeSolicitud implements Serializable{

	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@JsonIgnoreProperties( allowSetters = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solicitud", nullable = false)
	private Solicitud solicitud;
	
	@NotNull (message = "tablaCambiar{NotNull}")
	@Column(name = "tablaCambiar",  length = 100,nullable = false)
	private String tablaCambiar;
	
	@NotNull (message = "valor Anterior {NotNull}")
	@Column(name = "valorAnterior",length = 100, nullable = false)
	private String valorAnterior;
	
	@NotNull (message = "valor Actual {NotNull}")
	@Column(name = "valorActual",length = 100, nullable = false)
	private String valorActual;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechaHistorial", nullable = true)
	private Date fechaHistorial;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_modificacion", nullable = true)
	private Cuenta usuarioModificacion;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_modificacion_interno", nullable = true)
	private CuentaInterno usuarioModificacionInterno;
	
	@NotNull (message = "observacion{NotNull}")
	@Column(name = "observacion", length = 200, nullable = false)
	private String observacion;
	
	@Column(name = "comprobanteRuta",length = 200, nullable = true)
	private String comprobanteRuta;
	
	@PrePersist
	public void prePersist() {
		this.fechaHistorial= new Date();
	}
}
