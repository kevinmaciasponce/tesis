package com.multiplos.cuentas.pojo.amortizacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.context.properties.ConstructorBinding;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class AmortizacionResponse {
	private Long id;
	private String cliente;
	private String pais;
	private String nombreProyecto;
	private String codigoProyecto;
	private BigDecimal montoInversion;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDateTime fechaCreacion;
	private int plazo;
	private BigDecimal rendimientoNeto;
	private BigDecimal rendimientoTotalInversion;
	private BigDecimal totalRecibir;
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate fechaEfectiva;
	private List<AmortizacionDetalleResponse> detallesTblAmortizacion;
	
	
	
public AmortizacionResponse(Long id,String cliente,String pais,String nombreProyecto,String codigoProyecto,BigDecimal inversion,
		LocalDateTime fechaGeneracion,int Plazo,BigDecimal rendimientoNeto,BigDecimal rendimientoTotal,BigDecimal totalRecibir,LocalDate fechaEfectiva) {
	this.id=id;
	this.cliente= cliente;
	this.pais=pais;
	this.nombreProyecto= nombreProyecto;
	this.codigoProyecto= codigoProyecto;
	this.montoInversion=inversion;
	this.fechaCreacion= fechaGeneracion;
	this.plazo= Plazo;
	this.rendimientoNeto= rendimientoNeto;
	this.rendimientoTotalInversion= rendimientoTotal;
	this.totalRecibir= totalRecibir;
	this.fechaEfectiva=fechaEfectiva;
	
	
}
}
