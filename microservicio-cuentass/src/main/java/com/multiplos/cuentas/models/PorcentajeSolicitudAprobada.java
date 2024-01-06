package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_porc_sol_aprobadas", schema = "promotor")
public class PorcentajeSolicitudAprobada implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Type(type = "big_decimal")
	@Column(name = "monto_solicitado", precision = 12, scale = 2, nullable = true)
	private BigDecimal montoSolicitado;
	
	@Type(type = "big_decimal")
	@Column(name = "monto_aprobado", precision = 12, scale = 2, nullable = true)
	private BigDecimal montoAprobado;

	@Type(type = "big_decimal")
	@Column(name = "porcentaje_aprobado", precision = 5, scale = 2,nullable = false)
	private BigDecimal porcentajeAprobado;
	
	@Column(name = "fecha_aprobacion", nullable = false)
	private LocalDate fechaAprobacion;
	
	@Column(name = "usuario_aprobacion", length = 50, nullable = false)
	private String usuarioAprobacion;
	
	/*@Column(name = "codigo_proyecto", length = 50, nullable = false)
	private String codigoProyecto;*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_proyecto", nullable = false)
	private Proyecto proyecto;
	
	@Column(name = "observacion", length = 200, nullable = false)
	private String observacion;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@JsonIgnoreProperties(value = {"porcentajeSolicitudAprobada"}, allowSetters = true)
	@OneToMany(mappedBy = "porcentajeSolicitudAprobada", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<DetallePorcentajeSolicitudAprobada> detallePorcSolAprobada;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
	
}
