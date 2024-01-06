package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "mult_datos_inversion", schema = "inversion")
@AllArgsConstructor
@NoArgsConstructor
public class DatosInversion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_dato")
	private Long idDato;
	
	@JsonIgnoreProperties(value = {"datosInversion"}, allowSetters = true)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solicitud", nullable = true)
	private Solicitud solicitud;
	
	@Column(name = "tabla_amortizacion", nullable = true)
	private boolean tablaAmortizacion;
	
	@Column(name = "formulario", nullable = true)
	private boolean formulario;
	
	@Column(name = "documentacion", nullable = true)
	private boolean documentacion;
	
	@Column(name = "pago", nullable = true)
	private boolean pago;
	
}
