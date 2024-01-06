package com.multiplos.cuentas.auspicios.models;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.PersonaTipoCuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_beneficiario", schema = "auspicios",
indexes = {@Index(name="idx01_bene_ident", columnList = "id"),
		@Index(name="idx01_bene_person", columnList = "id_persona"),
		@Index(name="idx02_bene_ident_repre", columnList = "id, id_representante ")})
public class Beneficiario implements Serializable {

	private static final long serialVersionUID = 1L; 
	@Id
	@Column(name = "id")
	private String id;
	
	@OneToOne(fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	@JoinColumn(name = "id_persona", updatable = false, nullable = false, unique=true)
	private Persona persona;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_representante", updatable = false, nullable = true)
	private Persona representanteLegal;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "disciplina ",nullable = false)
	private Disciplina disciplina;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="categoria",  nullable = false)
	private Categorias categoria;	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="modalidad", nullable = false)
	private Modalidad modalidad;
	
	@Column(name="correo",nullable = false)
	@Email(message = "correo no valido")
	private String correo;
	
	@Column(name="perfil" ,length = 700, nullable = false)
	private String perfil;
	
	@Column(name = "ruta_foto1",length = 300, nullable = true)
	private String ruta1;
	
	@Column(name = "ruta_foto2",length = 300, nullable = true)
	private String ruta2;
	
	@Column(name = "titulo_actual",length = 50, nullable = true)
	private String tituloActual;
	
	@Column(name = "activo")
	private Boolean activo;
	
	@OneToOne(fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	@JoinColumn(name="cuenta_bancaria",nullable = true)
	private PersonaTipoCuenta cuentaBancaria;
	
	@JsonIgnoreProperties(value = {"beneficiario"}, allowSetters = true,allowGetters = true)
	@OneToMany(mappedBy = "beneficiario", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<TitulosDeportivos> titulos;

	@JsonIgnoreProperties(value = {"beneficiario"}, allowSetters = true)
	@OneToMany(mappedBy = "beneficiario", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	private List<AuspicioValoracion> valoraciones;
	
	@JsonIgnoreProperties(value = {"beneficiario"}, allowSetters = true)
	@OneToMany(mappedBy = "beneficiario", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
	private List<Auspicio> auspicios;
	
	
	public void newCategoria(Long id) {
		Categorias cat=  Categorias.builder().id(id).build();
		this.categoria= cat;
	}
	public void newDisciplina(Long id) {
		Disciplina var=  Disciplina.builder().id(id).build();
		this.disciplina= var;
	}
	public void newModalidad(Long id) {
		Modalidad mod=  Modalidad.builder().id(id).build();
		this.modalidad= mod;
		
	}
	
	@PrePersist
	public void prePersist() {
		this.activo=true;
		this.valoraciones= new ArrayList<>();
		this.auspicios= new ArrayList<>();
		this.titulos= new ArrayList<>();
	}
	
}
