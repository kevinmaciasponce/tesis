package com.multiplos.cuentas.models;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_doc_aceptados", schema = "inversion",
		indexes = {@Index(name="idx01_docu_tipoDocu", columnList = "id_tipo_documento, estado")})
public class DocumentoAceptado implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_doc_aceptado")
	private Long idDocAceptado;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_documento", nullable = false)
	private TipoDocumento documento;
	
	@ManyToOne
    @JoinColumn(name = "identificacion", nullable = false, updatable = false)
    private Persona persona;
	
	@Column(name = "numero_solicitud",length = 20, nullable = true)
	private String numeroSolicitud;
	
	@Column(name = "nombre",length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "ruta",length = 200, nullable = false)
	private String ruta;
	
	@Column(name = "version", nullable = false)
	private int version;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "usuario_creacion",length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;
	
	@Column(name = "usuario_modificacion",length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 10, nullable = false)
	private String estado;
	
	@Column(name = "habilitado", nullable = false)
	private boolean habilitado;
	
	@PrePersist
	public void prePersist() {
		this.estado = "Aceptado";
	}

}
