package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_tabla_amortizacion", schema = "inversion")
		//indexes = {@Index(name="idx01_tblamort_soliTablEsta", columnList = "numero_solicitud, id_tipo_tabla, estado"),
				//	@Index(name="idx02_tblamort_cproTablEsta", columnList = "codigo_proyecto, id_tipo_tabla, estado")})
public class TablaAmortizacion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tbl_amortizacion")
	private Long idTblAmortizacion;
	
	@Type(type = "big_decimal")
	@Column(name = "monto_inversion", precision = 12, scale = 2, nullable = false)
	private BigDecimal montoInversion;
	
	@Column(name = "plazo", nullable = false)
	private int plazo;
	

	@JsonFormat(pattern="dd/MM/yyyy")
	@Column(name = "fecha_efectiva", nullable = true)
	private LocalDate fechaEfectiva;
	
	@Type(type = "big_decimal")
	@Column(name = "rendimiento_neto", precision = 12, scale = 2, nullable = false)
	private BigDecimal rendimientoNeto;
	
	@Type(type = "big_decimal")
	@Column(name = "rendimiento_total_inversion", precision = 12, scale = 2, nullable = true)
	private BigDecimal rendimientoTotalInversion;
	
	@Type(type = "big_decimal")
	@Column(name = "total_recibir", precision = 12, scale = 2, nullable = true)
	private BigDecimal totalRecibir;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_creacion", nullable = true)
	private Cuenta usuarioCreacion;
	
	@JsonIgnoreProperties(value = {"tablaAmortizacion"}, allowSetters = true)
	@OneToMany(mappedBy = "tablaAmortizacion", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<DetalleAmortizacion> detallesTblAmortizacion;
		
	@OneToOne
	@JoinColumn(name = "id_tipo_tabla", nullable = false)
	private TipoTabla tipoTabla;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
		
//	@Column(name = "numero_solicitud",length = 20/*, nullable = false*/)
//	private String numeroSolicitud;
	
//	@Column(name = "fecha_modificacion", nullable = true)
//	private LocalDateTime fechaModificacion;
//	
//	@Column(name = "usuario_modificacion", length = 50, nullable = true)
//	private String usuarioModificacion;
	
//	@Column(name = "fecha_generacion", nullable = false)
//	private LocalDate fechaGeneracion;
	
//	@Column(name = "plazo_remanente", nullable = true)
//	private int plazoRemanente;
	
	
	
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "codigo_proyecto", updatable = false, nullable = true)
//	private Proyecto proyecto;
	
	@PrePersist
	public void prePersist() {
		this.estado= "A";
		this.fechaCreacion= LocalDateTime.now();
	}
}
