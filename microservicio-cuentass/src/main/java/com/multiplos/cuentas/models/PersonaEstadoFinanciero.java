package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_est_finan", schema = "cuenta")
public class PersonaEstadoFinanciero implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_est_finan")
	private Long idEstFinan;
	
	@Type(type = "big_decimal")
	@Column(name = "ingreso_anual", precision = 12, scale = 2, nullable = false)
	private BigDecimal ingresoAnual;
	
	@Type(type = "big_decimal")
	@Column(name = "egreso_anual", precision = 12, scale = 2, nullable = false)
	private BigDecimal egresoAnual;
	
	@Type(type = "big_decimal")
	@Column(name = "total_activo", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalActivo;
	
	@Type(type = "big_decimal")
	@Column(name = "total_pasivo", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalPasivo;
	
	@Type(type = "big_decimal")
	@Column(name = "total_patrimonio", precision = 12, scale = 2, nullable = false)
	private BigDecimal totalPatrimonio;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}

}
