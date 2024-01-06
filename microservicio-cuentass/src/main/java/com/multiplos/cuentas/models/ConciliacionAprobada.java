package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@Entity
@Table(name = "mult_conciliacion_aprobada", schema = "negocio",
		indexes = {@Index(name="idx01_estadoConciliacionAprobada", columnList = "estado")})


public class ConciliacionAprobada implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id", unique = true)
		private Long id;
		
		@Column(name = "usuario", length = 50, nullable = false)
		private String usuario;
		
		@Column(name = "fecha", nullable = false)
		@Temporal(TemporalType.TIMESTAMP)
		@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
		private Date fecha;
		
		@Type(type = "big_decimal")
		@Column(name = "monto_conciliado", precision = 12, scale = 2, nullable = false)
		private BigDecimal montoConciliado;
		
		@JsonIgnoreProperties(value = {"idConciliacion"}, allowSetters = true)
		@OneToMany(mappedBy = "idConciliacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
		private List<ConciliacionAprobadaDetalle> detalleConciliaciones;
		
		@Column(name = "estado",length = 1, nullable = false)
		private String estado;
		
		@PrePersist
		public void prePersist() {
			this.estado = "A";
			this.fecha=new Date();
		}
		
	
	
	
}
