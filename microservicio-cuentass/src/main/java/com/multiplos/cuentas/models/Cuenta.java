package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_cuentas", schema = "cuenta",
		indexes = {@Index(name="idx01_cuenta_id", columnList = "id_cuenta"),
				@Index(name="idx02_cuenta_mail", columnList = "email"),
				@Index(name="idx03_cuenta_mail_status", columnList = "email ,estado ")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cuenta implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cuenta", length = 50, nullable = false)
	private String idCuenta;
	
	@Column(name = "email", length = 100, unique = true, nullable = false)
	private String email;
	
	@Column(name = "usuario", length = 50, unique = true, nullable = false)
	private String usuario;
	
	@Column(name = "clave", length = 100, nullable = false)
	private String clave;
	
	@Column(name = "tipo_contacto",length = 10, nullable = false)
	private String tipoContacto;
	
	@Column(name = "usuario_contacto", length = 50, nullable = false)
	private String usuarioContacto;
	
	@Column(name = "acepta_politica_privacidad", length = 1, nullable = false)
	private String aceptaPoliticaPrivacidad;
	
	@Column(name = "acepta_termino_uso", length = 1, nullable = false)
	private String aceptaTerminoUso;
	
	@Column(name = "cuenta_activa", length = 1, nullable = true)
	private String cuentaActiva;
	
	@Column(name = "inicios_erroneos", nullable = true)
	private int iniciosErroneos;
	
	@Column(name = "fecha_creacion", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreacion;
	
	@Column(name = "usuario_creacion",length = 50, nullable = false)
	private String usuarioCreacion;
	
	@Column(name = "fecha_modificacion", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaModificacion;
	
	@Column(name = "usuario_modificacion",length = 50, nullable = true)
	private String usuarioModificacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	

	@OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "id_persona", referencedColumnName = "identificacion")
	private Persona persona;
	
//	@OneToOne
//	@JoinColumn(name = "id_rol", nullable = false)
//	private Rol rol;
	
	@JsonIgnoreProperties(value = {"cuenta"}, allowSetters = true)
	@OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	private List<Roles> roles= new ArrayList<>();
	
	@Column(name = "acepta_recibir_informacion", length = 1, nullable = false)
	private String aceptaRecibirInformacion;

	public Cuenta(String identificacion) {
		this.idCuenta=identificacion;
	}
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
		this.fechaCreacion = new Date();
		this.cuentaActiva="S";
		this.estado="A";
		this.encriptaClave();
		//this.roles=new ArrayList<>();
	}
		
}
