package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;
import org.springframework.stereotype.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_auspicios_valoracion", schema = "auspicios", 
indexes = {
		@Index(name="idx01_val_aus_bene", columnList = "beneficiario"),
		@Index(name="idx01_val_aus_beneXanio", columnList = "beneficiario , anio",unique = true),
		@Index(name="idx01_val_aus_beneXanio", columnList = "beneficiario , activo")
		})

public class AuspicioValoracion implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="beneficiario",updatable = false, nullable = false)
	private Beneficiario beneficiario;	

	@Column(name ="anio", updatable = false, nullable = false)
	private int anio;	
	
	@Column(name ="calificacion",length = 50, nullable = false)
	private String calificacion;	
	
	@Column(name ="fecha_calificacion", nullable = false)
	private LocalDate fechaCalificacion;
	
	@Column(name ="fecha_caducidad", nullable = false)
	private LocalDate fechaCaducidad;
	
	@Column(name ="presupuesto_aprobado", precision = 12, scale = 2 , nullable = false)
	private BigDecimal presupuestoAprobado;
	
	@Column(name ="presupuesto_recaudado", precision = 12, scale = 2 ,nullable = false)
	private BigDecimal presupuestoRecaudado;
	
	@Column(name ="presupuesto_restante", precision = 12, scale = 2 ,nullable = false)
	private BigDecimal presupuestoRestante;
	
	@Column(name ="ruta_documento",length = 300,nullable  = false)
	private String ruta;
	
	@Column(name="bianual",nullable = false)
	private Boolean bianual;
	
	@Column(name="activo",nullable = false)
	private Boolean activo;
	
	@PrePersist
	public void prePersist() {
		this.presupuestoRestante= this.presupuestoAprobado.subtract(presupuestoRecaudado);
		if(this.activo==null) {
			this.activo=true;
		}
		
	}
	
}
