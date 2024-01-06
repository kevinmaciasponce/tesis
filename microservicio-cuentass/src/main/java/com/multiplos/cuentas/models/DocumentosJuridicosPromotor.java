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
@Table(name = "mult_documentos_juridicos", schema = "promotor",
		indexes = {@Index(name="idx01_pro_docJur_empresa", columnList = "empresa"),
				   @Index(name="idx03_pro_docJur_empresaEstado", columnList = "empresa, activo")})
public class DocumentosJuridicosPromotor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="empresa",updatable = false)
	private Empresa empresa;
	
	@Column(name = "escritura")
	private String escritura;
	
	@Column(name = "estatutos_vigentes")
	private String estatutosVigentes;
	
	@Column(name = "ruc_vigente")
	private String rucVigente;
	
	@Column(name = "nombramiento_rl")
	private String nombramientoRl; 
	
	@Column(name = "cedula_rl")
	private String cedulaRl;
	
	
	@Column(name = "nomina_accionista")
	private String nominaAccionista;
	
	@Column(name = "identificaciones_accionista")
	private String identificacionesAccionista;
	
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
	
	public DocumentosJuridicosPromotor(DocumentosJuridicosPromotor target) {
		this.activo=target.activo;
		this.cedulaRl= target.cedulaRl;
		this.empresa=target.empresa;
		this.escritura=target.escritura;
		this.estatutosVigentes=target.estatutosVigentes;
		this.fechaCreacion=target.fechaCreacion;
		this.identificacionesAccionista=target.identificacionesAccionista;
		this.nombramientoRl=target.nombramientoRl;
		this.nominaAccionista=target.nominaAccionista;
		this.rucVigente=target.rucVigente;
		this.userCreacion=target.userCreacion;
	}
	
	 public  DocumentosJuridicosPromotor clone() {
		 
		return new DocumentosJuridicosPromotor(this);
		 
	 }
	
	
}
