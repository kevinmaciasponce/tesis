package com.multiplos.cuentas.models;

import java.io.Serializable;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_info_adicional", schema = "cuenta",
		indexes = {@Index(name="idx01_persInfo_idPersona", columnList = "id_persona, estado")})
public class PersonaInfoAdicional implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_info_adicional")
	private Long idInfoAdicional;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona", updatable = false, nullable = false,unique=true)
	private Persona persona;
	
	@Column(name = "estado_civil", length = 15, nullable = true)
	private String estadoCivil;
	
	@Column(name = "sexo", length = 1, nullable = true)
	private String sexo;
	
	@Column(name = "numero_telefono", length = 10, nullable = true)
	private String numeroTelefono;
	
	@Column(name = "fuente_ingresos", length = 15, nullable = false)
	private String fuenteIngresos;
	
	@Column(name = "cargo_persona", length = 15, nullable = true)
	private String cargoPersona;
	
	@Column(name = "actividad_economica", length = 100, nullable = true)
	private String actividadEconomica;
	
	@Column(name = "residente_domicilio_fiscal", length = 1, nullable = false)
	private String residenteDomicilioFiscal;
	
	@Column(name = "pais_domicilio_fiscal", nullable = true)
	private Long paisDomicilioFiscal;
	
	@Column(name = "fecha_registro", nullable = false)
	private LocalDate fechaRegistro;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_creacion", nullable = false)
	private Cuenta usuarioCreacion;

	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_modificacion",  nullable = true)
	private Cuenta usuarioModificacion;

	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@OneToOne
	@JoinColumn(name = "id_domicilio", nullable = false)
	private PersonaDomicilio persDomicilio;
	
	@OneToOne(fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	@JoinColumn(name = "id_tipo_cuenta", nullable = false)
	private PersonaTipoCuenta persTipoCuenta;
	
	@OneToOne
	@JoinColumn(name = "id_est_finan_jur", nullable = true)
	private PersonaEstadoFinanciero persEstadoFinanciero;
	
	@OneToOne
	@JoinColumn(name = "id_repre_legal_jur", nullable = true)
	private PersonaRepresentanteLegal persRepreLegal;
	
	@OneToOne
	@JoinColumn(name = "id_firma_jur", nullable = true)
	private PersonaFirma persFirma;
	

	@OneToOne( fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "id_doc_identificacion", nullable = true)
	private PersonaDocumento personaDocumentos;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}

}
