package com.multiplos.cuentas.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.annotation.Generated;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_documentos_financieros", schema = "promotor",
		indexes = {@Index(name="idx01_pro_docFin_empresa", columnList = "empresa"),
				   @Index(name="idx03_pro_docFin_empresaEstado", columnList = "empresa, activo")})
public class DocumentosFinancierosPromotor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="empresa",updatable = false)
	private Empresa empresa;
	
	
	@Column(name="impuesto_renta_aa",updatable = false)
	private String impuestoRentaAnioAnterior;
	
	@Column(name = "estado_financiero_aa")
	private String estadoFinancieroAnioAnterior;
	
	@Column(name = "estado_financiero_actual")
	private String estadoFinancieroActuales;
	
	@Column(name = "anexo_cts_cobrar")
	private String anexoCtsCobrar;
	
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion")
	private Date fechaCreacion;
	
	@Column(name = "usuario_creacion")
	private String userCreacion;
		

	@Column(name = "activo")
	private Boolean activo;
	
	@PrePersist
	public void prePersist() {
		this.fechaCreacion= new Date();
		this.activo=true;
	}
	
	public DocumentosFinancierosPromotor(DocumentosFinancierosPromotor target) {
		this.activo=target.activo;
		this.empresa=target.empresa;
		this.fechaCreacion=target.fechaCreacion;
		this.userCreacion=target.userCreacion;
		
		this.anexoCtsCobrar=target.anexoCtsCobrar;
		this.estadoFinancieroActuales=target.estadoFinancieroActuales;
		this.estadoFinancieroAnioAnterior=target.estadoFinancieroAnioAnterior;
		this.impuestoRentaAnioAnterior=target.impuestoRentaAnioAnterior;
		
	}
	
	 public  DocumentosFinancierosPromotor clone() {
		 
		return new DocumentosFinancierosPromotor(this);
		 
	 }
	
	
}
