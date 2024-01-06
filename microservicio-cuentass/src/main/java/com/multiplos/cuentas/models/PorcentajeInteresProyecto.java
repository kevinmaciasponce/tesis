package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_porc_interes_tbl_proyecto", schema = "negocio",
			indexes = {@Index(name="idx01_porc_inte_tbl_proy_codProy", columnList = "codigo_proyecto")})
public class PorcentajeInteresProyecto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_proyecto", nullable = false)
	private Proyecto proyecto;
	
	@Type(type = "big_decimal")
	@Column(name = "porcentaje_interes", precision = 5, scale = 2,nullable = false)
	private BigDecimal porcentajeInteres;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_tabla", nullable = false)
	private TipoTabla tipoTabla;
	
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
