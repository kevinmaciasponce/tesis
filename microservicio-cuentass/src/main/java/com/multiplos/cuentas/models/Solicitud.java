package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.Persistent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_solicitudes", schema = "inversion",
		indexes = {@Index(name="idx01_sol_numSolicitud", columnList = "numero_solicitud"),
				   @Index(name="idx03_sol_numSolEstaActu", columnList = "numero_solicitud, estado_actual"),
				   @Index(name="idx04_sol_numSolAmort", columnList = "numero_solicitud, tabla_amortizacion"),
		@Index(name="idx05_sol_numSolInvestor", columnList = "numero_solicitud, id_inversionista"),
		@Index(name="idx06_sol_numSolUserCreacion", columnList = "numero_solicitud, usuario_creacion"),
		@Index(name="idx07_sol_numSolProyecto", columnList = "numero_solicitud, codigo_proyecto"),
		@Index(name="idx08_sol_numSolPagare", columnList = "numero_solicitud, pagare")
				   /*,
				   @Index(name="idx04_sol_codProyEstaActFechVige", columnList = "codigo_proyecto, estado_actual, fecha_vigencia"),
				   @Index(name="idx05_sol_codProyEstaActFechVige", columnList = "codigo_proyecto, estado_actual, fecha_vigencia")*/})
public class Solicitud implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "numero_solicitud",length = 20, nullable = false)
	private Long numeroSolicitud;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inversionista", nullable = false, updatable = false)
    private Cuenta inversionista;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_creacion", nullable = false, updatable = false)
	private Cuenta  usuarioCreacion;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_solicitud", nullable = true)
	private TipoSolicitud tipoSolicitud;
	
	@Column(name = "observacion",length = 100, nullable = true)
	private String observacion;
	
	@OneToOne
	@JoinColumn(name = "estado_actual", nullable = false)
	private TipoEstado estadoActual;
	
	@Column(name = "acepta_licitud_fondos", length = 1, nullable = true)
	private String aceptaLicitudFondos;
	
	@Column(name = "acepta_informacion_correcta", length = 1, nullable = true)
	private String aceptaInformacionCorrecta;
	
	@Column(name = "acepta_ingresar_info_vigente", length = 1, nullable = true)
	private String aceptaIngresarInfoVigente;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_generacion",nullable = false)
	private Date fechaGeneracion;
	
	@ManyToOne
	@JoinColumn(name = "codigo_proyecto", nullable = false)
	private Proyecto proyecto;
	
	@JsonIgnoreProperties(value = {"solicitud"}, allowSetters = true)
	@OneToOne(mappedBy = "solicitud",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_datos_inversion", nullable = true)
	private DatosInversion datosInversion;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "tabla_amortizacion", nullable = false )
	private TablaAmortizacion amortizacion;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "pagare", nullable = true )
	private TablaAmortizacion pagare;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "documentos", nullable = true )
	private SolicitudDocumentos documentos;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ultimo_historial", nullable = true )
	private HistorialDeSolicitud historial;
	
	@JsonIgnoreProperties(value = {"solicitud"}, allowSetters = true)
	@OneToMany(mappedBy = "solicitud",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//	@JoinColumn(name = "transaccion", nullable = true)
	private List<Transaccion> transaccion;
	
	@Column(name = "activo",length = 1, nullable = false)
	private Boolean activo;
		
	public Solicitud () {}
	public Solicitud (Long numeroSolicitud) {
		this.numeroSolicitud=numeroSolicitud;
	}
	
	@PrePersist
	public void prePersist() {
		this.activo=true;
		this.fechaGeneracion= new Date();
	}
	
//	@OneToOne
//	@JoinColumn(name = "estado_anterior", nullable = true)
//	private TipoEstado estadoAnterior;
	
//	@Column(name = "fecha_vigencia",nullable = true)
//	private LocalDateTime fechaVigencia;
	
//	@Column(name = "tipo_contacto",length = 15, nullable = true)
//	private String tipoContacto;
//	
//	@Column(name = "usuario_contacto", length = 50, nullable = false)
//	private String usuarioContacto;
	
	
//	@Column(name = "tipo_persona",length = 3, nullable = false)
//	private String tipoPersona;
//	
//	@Column(name = "usuario", length = 50, nullable = false)
//	private String usuario;
	
}
