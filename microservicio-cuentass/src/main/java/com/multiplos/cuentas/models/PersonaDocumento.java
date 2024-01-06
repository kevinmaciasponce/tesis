package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "mult_pers_documentos", schema = "cuenta")
@Data
public class PersonaDocumento implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_documento")
	private Long idDocumento;
	
	@Column(name = "nombre",length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "ruta",length = 200, nullable = false)
	private String ruta;
	
	@Column(name = "nombre_post",length = 100, nullable = true)
	private String nombrepost;
	
	@Column(name = "ruta_post",length = 200, nullable = true)
	private String rutapost;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;
	
	@Column(name = "usuario_modificacion", length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	//@JsonIgnoreProperties(value = {"personaDocumentos"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_info_adicional", nullable = false)
	private PersonaInfoAdicional personaInfoAdicional;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_documento", nullable = false)
	private TipoDocumento documento;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
}
