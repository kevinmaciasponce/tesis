package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "mult_personas", schema = "cuenta",
		indexes = {@Index(name="idx01_pers_ident", columnList = "identificacion"),
				@Index(name="idx02_pers_ident_status", columnList = "identificacion, estado")})

public class Persona implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "identificacion", length = 13, nullable = false)
	private String identificacion;
	
	@Column(name = "tipo_cliente", length = 15, nullable = true)
	private String tipoCliente;
	
	@Column(name = "tipo_persona", length = 3, nullable = false)
	private String tipoPersona;
	
	@Column(name = "tipo_identificacion", length = 3, nullable = false)
	private String tipoIdentificacion;
	
	@Column(name = "nacionalidad", length = 2, nullable = false)
	private String nacionalidad;
	
	@Column(name = "nombres", length = 50, nullable = true)
	private String nombres;
	
	@Column(name = "apellidos", length = 50, nullable = true)
	private String apellidos;
	
	@Column(name = "genero", length = 1, nullable = true)
	private String genero;
	
	@Column(name = "fecha_nacimiento", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date fechaNacimiento;
	
	@Column(name = "numero_celular", length = 14, nullable = false)
	private String numeroCelular;
	
	@Column(name = "razon_social", length = 100, nullable = true)
	private String razonSocial;
	
	@Column(name = "nombre_contacto", length = 100, nullable = true)
	private String nombreContacto;
	
	@Column(name = "cargoContacto", length = 50, nullable = true)
	private String cargoContacto;
	
	@Column(name = "email_contacto", length = 100, nullable = true)
	private String emailContacto;
	
	@Column(name = "anio_inicio_actividad", nullable = true)
	private int anioInicioActividad;
	
	@Column(name = "fecha_creacion", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreacion;
	
	@Column(name = "usuario_creacion", length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaModificacion;
	
	@Column(name = "usuario_modificacion", length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@JsonIgnoreProperties(value = {"persona"}, allowSetters = true)
	@OneToOne(mappedBy = "persona",fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
    private Cuenta cuenta;
	
	@JsonIgnoreProperties(value = {"persona"}, allowSetters = true)
	@OneToOne(mappedBy = "persona",fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	private PersonaInfoAdicional persInfoAdicional;
	
	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
		this.estado="A";
	}
	
	public Persona(String id) {
		this.identificacion=id;
	}

}
