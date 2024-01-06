package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_hist_proyecto", schema = "historicas")
public class HistorialDeProyecto {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull (message = "idProyecto {NotNull}")
	@JsonIgnoreProperties( allowSetters = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProyecto", nullable = false)
	private Proyecto proyecto;
	
	@NotNull (message = "tablaCambiar{NotNull}")
	@Column(name = "tablaCambiar", nullable = false)
	private String tablaCambiar;
	
	@NotNull (message = "valor Anterior {NotNull}")
	@Column(name = "valorAnterior", nullable = false)
	private String valorAnterior;
	
	@NotNull (message = "valor Actual {NotNull}")
	@Column(name = "valorActual", nullable = false)
	private String valorActual;
	
	
	@Column(name = "fechaHistorial", nullable = true)
	private LocalDateTime fechaHistorial;
	
	@Column(name = "usuarioCreacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
	@NotNull (message = "observacion{NotNull}")
	@Size(min = 4, max = 200, message = "observacion {Size.mensaje}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "observacion {Pattern.letras}")
	@Column(name = "observacion", length = 200, nullable = false)
	private String observacion;
	
	
	@Column(name = "comprobanteRuta",length = 200, nullable = true)
	private String comprobanteRuta;
	
}
