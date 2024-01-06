package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

//import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_detalle_amortizacion", schema = "inversion",
		indexes = {@Index(name="idx01_detaAmort_idTblAmort", columnList = "id_tbl_amortizacion, estado")})
public class DetalleAmortizacion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_det_amortizacion")
	private Long idDetAmortizacion;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tbl_amortizacion", nullable = false)
    private TablaAmortizacion tablaAmortizacion;
	
	@Column(name = "detalle_cobro",length = 50, nullable = false)
	private String detalleCobro;
	
	@Column(name = "fecha_estimacion", nullable = true)
	private LocalDate fechaEstimacion;
	
	@Column(name = "fecha_realizada", nullable = true)
	private LocalDate fechaRealizada;
	
	@Column(name = "fecha_registro", nullable = true)
	private LocalDate fechaRegistro;
	
	@Type(type = "big_decimal")
	@Column(name = "rendimiento_mensual", precision = 12, scale = 2, nullable = true)
	private BigDecimal rendimientoMensual;
	
	@Type(type = "big_decimal")
	@Column(name = "saldo_capital", precision = 12, scale = 2, nullable = true)
	private BigDecimal saldoCapital;
	
	@Type(type = "big_decimal")
	@Column(name = "cobros_capital", precision = 12, scale = 2, nullable = true)
	private BigDecimal cobrosCapital;
	
	@Type(type = "big_decimal")
	@Column(name = "total_recibir", precision = 12, scale = 2, nullable = true)
	private BigDecimal totalRecibir;
	
	@Column(name = "estado",length = 1, nullable = false)
	private String estado;

	@Column(name = "cuota", nullable = false)
	private int cuota;
	
	@Column(name = "ruta_pago",length = 300, nullable = true)
	private String rutaPago;
	
	@Column(name = "estado_pago",length = 30, nullable = true)
	private String estadoPago;
}
