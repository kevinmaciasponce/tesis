package com.multiplos.cuentas.pojo.promotor;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.multiplos.cuentas.models.CuentaException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaFormulario {

	private Long id;
	
	@NotBlank(message = "cuenta no puede estar vacio")
	private String cuenta;
	
	@NotBlank(message = "nombre no puede estar vacio")
	@Size(max=100,message = "nombre debe tener entre 100 caracteres")
	private String nombre;
	
	
	@NotNull(message = "actividad no puede estar vacio")
	private Long actividad;
	
	@NotNull(message = "pais no puede estar vacio")
	private Long pais;
	
	@NotBlank(message = "antecedente no puede estar vacio")
	private String antecedente;
	
//	@Size(max=100,message = "nombre debe tener entre 100 caracteres")
//	@NotBlank(message = "estado no puede estar vacio")
//	private String estado;
	
	@NotBlank(message = "Ventaja Competitiva no puede estar vacio")
	private String ventajaCompetitiva;
	
	@Size(max=50,message = "Usuario Compose debe tener entre 50 caracteres")
	@NotBlank(message = "Usuario Compose no puede estar vacio")
	private String userCompose;
	
	@Size(max=100,message = "Direccion debe tener entre 100 caracteres")
	@NotBlank(message = "direccion no puede estar vacio")
	private String direccion;
	
	@NotNull(message = "Anio inicio Actividad no puede estar vacio")
	private int anioInicioActividad;
	
	
	
	@Size(max=20,message = "Ciudad debe tener entre 20 caracteres")
	@NotBlank(message = "ciudad no puede estar vacio")
	private String ciudad;
	
	@Size(max=200,message = "Descripcion debe tener entre 200 caracteres")
	private String descripcionProducto;

	
//	@NotNull(message = "Margen de contribucion no puede estar vacio")
	private BigDecimal margenContribucion;
	
//	@NotNull(message = "Venta Totales  no puede estar vacio")
	private BigDecimal ventasTotales;
	
//	@NotNull(message = "Anio de venta y margen de contribucion no puede estar vacio")
	private Integer anio;
	
	
	public void validaDatosAnual()throws Exception {
		if(this.margenContribucion!= null && this.ventasTotales==null) {
			throw new CuentaException("Margen de contribucion no puede ir sin ventas totales ");
		}
		if(this.ventasTotales!= null && this.margenContribucion==null) {
			throw new CuentaException("Ventas totales no puede ir sin margen de contribucion ");
		}
		if(this.ventasTotales!=null && this.margenContribucion!=null && this.anio==null) {
			throw new CuentaException("Ventas totales y margen de contribucion no puede ir sin año ");
		}
	}
}
