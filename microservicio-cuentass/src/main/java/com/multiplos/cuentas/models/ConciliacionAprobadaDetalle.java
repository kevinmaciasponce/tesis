package com.multiplos.cuentas.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_conciliacion_aprobada_detalle", schema = "negocio")
public class ConciliacionAprobadaDetalle {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Long id;
	
	@Column(name = "nombre_banco",length = 20, nullable = false)
	private String nombreBanco;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conciliacion", nullable = false)
    private ConciliacionAprobada idConciliacion;
	
	@Column(name = "numero_cuenta",length = 50, nullable = false)
	private String numeroCuenta;
	
	@Column(name = "fecha_efectivo",length = 20, nullable = false)
	private LocalDate fechaEfectivo;
	
	@Column(name = "numero_comprobante",length = 20, nullable = false)
	private String numeroComprobante;
	
	@Type(type = "big_decimal")
	@Column(name = "monto", precision = 12, scale = 2, nullable = false)
	private BigDecimal monto;
	
	@Column(name = "lugar",length = 100, nullable = false)
	private String lugar;
	
	@Column(name = "tipo_transaccion",length = 20, nullable = false)
	private String tipoTransaccion;
	
	@Column(name = "concepto",length = 100, nullable = false)
	private String concepto;
	
	@Column(name = "observacion",length = 100, nullable = false)
	private String observacion;
	
	
}
