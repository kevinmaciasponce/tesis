package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_personal_interno", schema = "multiplo",
		indexes = {@Index(name="idx01_pers_int_ident", columnList = "id_pers_interno"),
					@Index(name="idx02_pers_cuenta", columnList = "cuenta_interna")})
public class PersonaInterna implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "id_pers_interno",length = 13)
	private String idPersInterno;
	
	/*@Column(name = "identificacion", length = 10, nullable = true)
	private String identificacion;*/
	
	
	@Column(name = "nombres", length = 50, nullable = false)
	private String nombres;
	
	
	@Column(name = "apellidos", length = 50, nullable = false)
	private String apellidos;
	
	
	@Column(name = "iniciales", length = 5, nullable = false)
	private String iniciales;
	
    @Column(name = "id_jefe", nullable = true)
    private Long idJefe;
	
    @JsonFormat(pattern = "YYYY-MM-DD")
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	
	@Column(name = "usuario_creacion",length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;
	
	@Column(name = "usuario_modificacion",length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	//@JsonIgnoreProperties(value = {"personalInterno"}, allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "cuenta_interna", unique = true,nullable = true)
	private CuentaInterno cuenta;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
		this.fechaCreacion= LocalDateTime.now();
	
	}
	@PreUpdate
	public void preUpdate() {
		this.estado = "A";
		this.fechaModificacion= LocalDateTime.now();
		this.usuarioModificacion= this.usuarioCreacion;
	
	}
}
