package com.multiplos.cuentas.auspicios.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.compress.archivers.zip.X000A_NTFS;
import org.springframework.stereotype.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.multiplos.cuentas.models.Persona;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mult_auspicios", schema = "auspicios",indexes = {
		@Index(name="idx01_beneficiarioXactivo", columnList = "beneficiario, activo"),
		@Index(name="idx02_AuspicioXestado", columnList = "estado"),
		@Index(name="idx02_AuspicioXBeneficiario", columnList = "beneficiario")
}
)

public class Auspicio implements Serializable{

	private static final long serialVersionUID = 1L; 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="beneficiario",updatable = false, nullable = false)
	private Beneficiario beneficiario;	
		
	@Column(name ="presupuesto_solicitado", precision = 12, scale = 2 , nullable = false)
	private BigDecimal presupuestoSolicitado;
	
	@Column(name ="presupuesto_recaudado", precision = 12, scale = 2 , nullable = false)
	private BigDecimal presupuestoRecaudado;
	
	@ManyToOne
	@JoinColumn(name="id_valoracion", nullable=false)
	private AuspicioValoracion valoracion;
	
	@OneToOne
	@JoinColumn(name="estado", nullable = false)
	private AuspicioEstados estado;
	
	@Column(name="activo", nullable = false)
	private Boolean activo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy/mm/dd hh:ss")
	@Column(name="fecha_generacion", nullable = true)
	private Date fechaGeneracion;
	
	@Column(name="observacion",nullable= true,length = 100)
	private String observacion;
	
	@JsonIgnoreProperties(value = {"auspicio"}, allowSetters = true)
	@OneToMany(mappedBy = "auspicio",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<AuspicioRecompensa> recompesas= new ArrayList<>();
	
	@JsonIgnoreProperties(value = {"auspicio"}, allowSetters = true)
	@OneToMany(mappedBy = "auspicio",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<AuspicioTorneo> torneosParticipar= new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.presupuestoRecaudado= new BigDecimal(0);
		this.activo=true;
		this.fechaGeneracion= new Date();
		//this.recompesas.stream().forEach((x)->{x.setAuspicio(this);});
	}
}
