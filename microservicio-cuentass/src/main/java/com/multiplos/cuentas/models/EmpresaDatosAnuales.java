package com.multiplos.cuentas.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mult_empresa_datos_anuales", schema = "promotor",indexes={@Index(name="indx01_idEmpresaXventas",columnList = "id_empresa,")})
public class EmpresaDatosAnuales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "margen_contribucion", precision = 12,scale = 2, nullable = true)
	private BigDecimal margenContribucion;
	
	@Column(name = "venta_totales",nullable = false, precision = 12, scale = 2)
	private BigDecimal ventasTotales;
	
	@ManyToOne
	@JoinColumn(name = "id_empresa",nullable = false)
	private Empresa empresa;
	
	@Column(name="anio",nullable = false)
	private Integer anio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion",nullable = false)
	private Date fechaCreacion;
	
	@Column(name = "usuario_creacion",nullable = false,length = 20)
	private String usuarioCreacion;
	
	@Column(name="activo")
	private Boolean activo;
	
	@PrePersist
	public void prePersist() {
		this.fechaCreacion= new Date();
		this.activo=true;
	}
	
	
	
}
