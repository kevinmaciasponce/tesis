package com.multiplos.cuentas.pojo.transaccion;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionResponse {

	private Long numeroSolicitud;
	private String numeroComprobante;
	private String rutaComprobante;
	private String UsuarioCreacion;
	private String Depositante;
	@JsonFormat 
	private LocalDate fechaDeposito;
	private String formaPago;
	private BigDecimal valor;
	private String estado;
	
	
	
	
	}
