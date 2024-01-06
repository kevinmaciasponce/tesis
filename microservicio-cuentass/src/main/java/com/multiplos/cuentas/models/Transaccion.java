package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_transacciones", schema = "inversion",
		indexes = {@Index(name="idx01_trans_soli", columnList = "numero_solicitud"),
				   @Index(name="idx02_trans_soliEsta", columnList = "numero_solicitud, estado"),
				   @Index(name="idx03_trans_conciliado", columnList = "numero_solicitud,conciliado"),
				   @Index(name="idx04_trans_comprobante", columnList = "numero_solicitud,numero_comprobante")})
public class Transaccion implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_doc_transaccion")
	private Long idDocTransaccion;
	
//	@Column(name = "nombre_cliente",length = 100, nullable = false)
//	private String nombreCliente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "numero_solicitud", nullable = true)
	private Solicitud solicitud;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proyecto", nullable = true)
	private Proyecto proyecto;
	
	@Column(name = "numero_comprobante",length = 20, nullable = false)
	private String numeroComprobante;
	
	@Column(name = "fecha_transaccion", nullable = false)
	private LocalDate fechaTransaccion;

	@Type(type = "big_decimal")
	@Column(name = "monto", precision = 12, scale = 2, nullable = false)
	private BigDecimal monto;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@Column(name = "conciliado", length = 1, nullable = true)
	private String conciliado;
	
	@Column(name = "nombre_documento",length = 100, nullable = true)
	private String nombreDocumento;
	
	@Column(name = "ruta_comprobante",length = 200, nullable = true)
	private String rutaComprobante;
	
	@OneToOne
	@JoinColumn(name = "id_forma_pago", nullable = false)
	private FormaPago formaPago;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_creacion", nullable = true)
	private Cuenta usuarioCreacion;
	
	@Column(name = "depositante", nullable = true)
	private String depositante;
	
//	@Column(name = "fecha_modificacion",nullable = true)
//	private LocalDateTime fechaModificacion;
//	
//	@Column(name = "usuario_modificacion",length = 50, nullable = true)
//	private String usuarioModificacion;
	
	@OneToOne
	@JoinColumn(name = "id_tipo_documento", nullable = false)
	private TipoDocumento documento;

	@PrePersist
	public void prePersist() {
		this.estado = "A";
		this.conciliado = "N";
	}

}
