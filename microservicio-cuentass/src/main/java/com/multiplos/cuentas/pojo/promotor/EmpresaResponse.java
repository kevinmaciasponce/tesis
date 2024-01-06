package com.multiplos.cuentas.pojo.promotor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpresaResponse {

	
	private Long id;
	private String nombre;
	private String nombreActividad;
	private String ruc;
	private String pais;
	private String direccion;
	private String ciudad;
	private Object margenContribucion;
	private Object ventasTotales;
	private String descripcionProducto;
	@JsonRawValue
	private String antecedentes;
	@JsonRawValue
	private String ventajaCompetitiva;
	
	
	
}
