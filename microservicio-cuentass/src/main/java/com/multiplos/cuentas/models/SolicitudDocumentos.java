package com.multiplos.cuentas.models;

import java.io.Serializable;
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

import lombok.Data;

@Data
@Entity
@Table(name = "mult_solicitudes_documentos", schema = "inversion",
		indexes = {@Index(name="idx01_sol_doc_numSolicitud", columnList = "solicitud"),
				   @Index(name="idx03_sol_doc_numSolEsta", columnList = "solicitud, estado")})
public class SolicitudDocumentos implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_sol_documentos")
	private Long idSolDocumentos;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solicitud", nullable = true)
	private Solicitud solicitud;
	
	@Column(name = "datos_inversionista",length = 300, nullable = false)
	private String datosInversionista;
	
	@Column(name = "contrato_prenda",length = 300, nullable = false)
	private String contratoPrendaRi;
	
	@Column(name = "modelo_contrato",length = 300, nullable = false)
	private String modeloContrato;
	
	@Column(name = "pagare",length = 300, nullable = false)
	private String pagare;
	
	@Column(name = "tabla_amortizacion",length = 300, nullable = false)
	private String tablaAmortizacion;
	
	@Column(name = "acuerdo_uso",length = 300, nullable = false)
	private String acuerdoUso;
	
	@Column(name = "observacion",length = 300, nullable = false)
	private String observacion;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion",nullable = true)
	private LocalDateTime fechaModificacion;
	
	@Column(name = "usuario_modificacion",length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
}
