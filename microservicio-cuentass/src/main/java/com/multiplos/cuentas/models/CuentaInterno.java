package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "mult_cuentas_internas", schema = "multiplo", indexes = {
				@Index(name="idx01_cuenta_int_id", columnList = "id_cuenta"),
				@Index(name="idx02_cuenta_int_mail", columnList = "email")})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CuentaInterno implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_cuenta",length =50,updatable= false)
	private String idCuenta;
	
	
	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;
	
	
	@Column(name = "clave", length = 100, nullable = false)
	private String clave;
	
//	@NotEmpty(message = "usuario no puede estar vacio")
//	@Size(min = 5,max = 50, message = "usuario debe tener entre 5 y 50 caracteres")
//	@Column(name = "usuario", length = 50, unique = true, nullable = false)
//	private String usuario;
	

	@Column(name = "cuenta_activa", length = 1, nullable = true)
	private String cuentaActiva;
	
	@Column(name = "inicios_erroneos", nullable = true)
	private int iniciosErroneos;
	
	
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
	
	/*@Column(name = "id_rol", nullable = false)
	private Long idRol;*/
//	@OneToOne
//	@JoinColumn(name = "id_rol", nullable = false)
//	private Rol rol;
//	
//	@OneToOne
//	@JoinColumn(name = "id_rol_int", nullable = true)
//	private RolInt rolInt;
	
	
	@JsonIgnoreProperties(value = {"cuentaInterna"}, allowSetters = true)
	@OneToMany(mappedBy = "cuentaInterna", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	private List<RolesInternos> roles;
	
	@OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "id_persona", nullable = false,updatable = false,unique = true )
	private PersonaInterna personalInterno;
	

	
	public String encriptaClave() {
		String encri=null; 
		if (!clave.isEmpty()) {
			BCryptPasswordEncoder encodex = new BCryptPasswordEncoder();
			 this.clave= encodex.encode(this.clave);
			 
		}
		return encri;
	}
	@PrePersist
	public void prePersist() {
		this.estado = "A";
		this.iniciosErroneos= 0;
		this.fechaCreacion= LocalDateTime.now();
		this.encriptaClave();
	}
	@PreUpdate
	public void preUpdate() {
		this.usuarioModificacion=this.usuarioCreacion;
		this.fechaModificacion=LocalDateTime.now();
		//this.clave= this.encriptaClave(this.clave);
	}
	
	public CuentaInterno(String idCuenta) {
		this.idCuenta= idCuenta;
		
		
	}
		
	
}
