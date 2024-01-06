package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mult_proyectos", schema = "promotor",
		indexes = {@Index(name="idx01_proyecto_idProyecto", columnList = "id_proyecto"),
				   @Index(name="idx02_proyecto_idProyEstAct", columnList = "id_proyecto, estado_actual"),
				   @Index(name="idx03_proyecto_estAct", columnList = "estado_actual")})
public class Proyecto implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_proyecto", length = 50, nullable = false)
	private String idProyecto;
	
	@Basic(optional = false)
	@Column(name = "ronda",nullable = true)
	private int ronda;
	
	@Column(name = "fecha_inicio_inversion", nullable = true)
	@JsonFormat(pattern="dd 'de' MMMM 'del' yyyy", locale = "es")
	private LocalDate fechaInicioInversion;

	@Column(name = "fecha_limite_inversion", nullable = true)
	@JsonFormat(pattern="dd 'de' MMMM 'del' yyyy", locale = "es")
	private LocalDate fechaLimiteInversion;

	@Type(type = "big_decimal")
	@Column(name = "tasa_efectiva_anual", precision = 5, scale = 2,nullable = false)
	private BigDecimal tasaEfectivaAnual;

	@Column(name = "tipo_inversion", length = 50, nullable = false)
	private String tipoInversion;

	@Column(name = "destino_financiamiento", length = 100, nullable = false)
	private String destinoFinanciamiento;//

	@Type(type = "big_decimal")
	@Column(name = "monto_solicitado", precision = 12, scale = 2, nullable = false)
	private BigDecimal montoSolicitado;
	
	@Type(type = "big_decimal")
	@Column(name = "monto_recaudado", precision = 12, scale = 2, nullable = true)
	private BigDecimal montoRecaudado;
	
	@Column(name = "plazo", nullable = false)
	private int plazo;
	
	@Column(name = "periodo_pago", nullable = false)
	private int periodoPago;
	
	@Column(name = "pago_interes", length = 15, nullable = false)
	private String pagoInteres;
	
	@Column(name = "pago_capital", length = 50, nullable = false)
	private String pagoCapital;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "usuario_creacion", length = 50, nullable = false, updatable = false)
	private String usuarioCreacion;

	@Column(name = "fecha_modificacion", nullable = true)
	private LocalDateTime fechaModificacion;

	@Column(name = "usuario_modificacion", length = 50, nullable = true)
	private String usuarioModificacion;

	
	@Column(name = "acepta_licitud_fondos", length = 1, nullable = true)
	private String aceptaLicitudFondos;
	
	@Column(name = "acepta_informacion_correcta", length = 1, nullable = true)
	private String aceptaInformacionCorrecta;
	
	@Column(name = "acepta_ingresar_info_vigente", length = 1, nullable = true)
	private String aceptaIngresarInfoVigente;
	
	
	/*@Column(name = "estado", length = 1, nullable = false)
	private String estado;*/
	@Basic(optional = false)
	@OneToOne
	@JoinColumn(name = "estado_actual", nullable = false)
	private TipoEstado estadoActual;
	
	@OneToOne
	@JoinColumn(name = "estado_anterior", nullable = true)
	private TipoEstado estadoAnterior;
	
	@ManyToOne
	@JoinColumn(name = "id_indicador", nullable = true)
	private Indicador indicadores;
	
	@Basic(optional = false)
	@ManyToOne
	@JoinColumn(name = "id_empresa", nullable = false)
	private Empresa empresa;

	@OneToOne()
	@JoinColumn(name = "calificacion_interna", updatable = true, nullable = true)
	private Calificacion calificacion;
	
	@OneToOne( cascade = CascadeType.ALL)
	@JoinColumn(name = "tabla_amortizacion", nullable = true)
	private TablaAmortizacion amortizacion;
	
	@JsonIgnoreProperties( allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ultimo_historial", nullable = true )
	private HistorialDeProyecto historial;
	
	@JsonIgnoreProperties(value = {"proyecto"}, allowSetters = true)
	@OneToMany(mappedBy = "proyecto", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<ProyectoRuta> proyectoRutas;
	
	@JsonIgnoreProperties(value = {"proyecto"}, allowSetters = true)
	@OneToMany(mappedBy = "proyecto",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<Transaccion> transacciones;

	public Proyecto(String id) {
		this.idProyecto=id;
	}
	@PrePersist
	public void prePersist() {
		this.montoRecaudado= new BigDecimal(0);
		this.fechaCreacion= LocalDateTime.now();
		this.estadoActual= new TipoEstado("BO");
//		if(this.fechaInicioInversion==null) {
//			this.fechaInicioInversion=LocalDate.parse(null);
//		}
	}
	@PreUpdate
	public void preUpdate() {
		this.usuarioModificacion= this.usuarioCreacion;
		this.fechaModificacion=  LocalDateTime.now();
	}
	


	
}
