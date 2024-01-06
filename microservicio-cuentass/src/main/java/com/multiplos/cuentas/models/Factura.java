package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mult_documentos_facturas", schema = "multiplo_documentos",
indexes = { @Index(name="idx01_cod_fact", columnList = "codigo_factura"),
			@Index(name="idx02_id_cliente", columnList = "id_cliente" ),
			@Index(name="idx02_estaado", columnList = "estado" )
})
public class Factura implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name = "numero_establecimiento",  nullable = false )
	private String numeroEstablecimiento;
	
	@Column(name = "numero_facturero",  nullable = false )
	private String numeroFacturero;
	
	@Column(name = "numero_documento", unique = true, nullable = false )
	private String numeroDoc;
	
	@Column(name = "codigo_factura", unique = true, nullable = false )
	private String codFact;
	
	@Column(name="id_cliente",nullable = false)
	private String idCliente;
	
	@Column(name="total_factura",precision = 12,scale = 2, nullable = false)
	private BigDecimal totalFactura;
	
	@Column(name="estado",nullable = false)
	private String estado;
	
	@JsonFormat(pattern = "YYYY-MM-DD")
	@Column(name= "fecha_emision", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEmision;
	
	@PrePersist
	public void prePersist() {
		this.fechaEmision= new Date();
		this.estado="Pendiente";
	}
	
//	@JsonRawValue
//	@ColumnTransformer(write = "?::jsonb")
//	private String detalle;

}
