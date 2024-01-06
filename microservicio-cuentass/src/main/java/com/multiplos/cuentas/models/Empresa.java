package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.tomcat.util.json.JSONParser;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.stereotype.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "mult_empresas", schema = "promotor",indexes={@Index(name="indx01_idEmpresaXcuenta",columnList = "id_empresa, cuenta")})
//@JsonIgnoreProperties(value = "cuenta")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Empresa implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_empresa")
	private Long idEmpresa;
		
	@Column(name = "nombre", unique = true, length = 100, nullable = false)
	private String nombre;
	
	@OneToOne
	@JoinColumn(name = "actividad", nullable = false)
	private TipoActividad actividad;
	
	@ColumnTransformer(write = "?::jsonb")
	@Column(name = "antecedente", columnDefinition = "json", nullable = false)
	@JsonRawValue
	private String antecedente;
	
	@ColumnTransformer(write = "?::jsonb")
	@Column(name = "ventaja_competitiva", columnDefinition = "json", nullable = false)
	@JsonRawValue
	private String ventajaCompetitiva;
	
	@OneToOne()
	@JoinColumn(name = "pais", nullable = false)
	private Pais pais;
	
	@Column(name = "ruc", length = 13, nullable = false,updatable = false)
	private String ruc;
	
	@Column(name = "direccion", length = 100, nullable = true,updatable = true)
	private String direccion;
	
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
	
	//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuenta",updatable = false)
	private Cuenta cuenta;
	
	@JoinColumn(name="dato_anual_actual")
	@OneToOne(cascade = CascadeType.ALL)
	private EmpresaDatosAnuales datosAnualActual;
	
	@Column(name = "ciudad", length = 20, nullable = true)
	private String ciudad;
	
	@Column(name = "descripcion_producto", length = 200, nullable = true)
	private String descripcionProducto;
	
	@JsonIgnoreProperties(value = "empresa", allowSetters = true,allowGetters = false)
	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
	private List<Proyecto> proyectos;
	
	@JsonIgnoreProperties( value = "empresa", allowSetters = true,allowGetters = false)
	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
	private List<DocumentosJuridicosPromotor> documentosJuridicos;
	
	@JsonIgnoreProperties( value = "empresa", allowSetters = true,allowGetters = false)
	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
	private List<DocumentosFinancierosPromotor> documentosFinancieros;
	

	
	@PrePersist
	public void PrePersist() {
		this.fechaCreacion= LocalDateTime.now();
		this.estado="A";
	}
	@PreUpdate
	public void PreUpdate() {
		this.usuarioModificacion= this.usuarioCreacion;
	}
	
	
	public Empresa(Long id) {
		this.idEmpresa= id;
	}
}
