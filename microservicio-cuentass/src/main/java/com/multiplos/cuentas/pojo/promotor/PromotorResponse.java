package com.multiplos.cuentas.pojo.promotor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotorResponse {

	private String razonSocial;
	public PromotorResponse(String razonSocial, String ruc, String correo, String cargo, String nombreContacto,
			String telefonoContacto, String pais, int anioInicio, int anioInicioAct) {
		super();
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.correo = correo;
		this.cargo = cargo;
		this.nombreContacto = nombreContacto;
		this.telefonoContacto = telefonoContacto;
		this.pais = pais;
		this.anioInicio = anioInicio;
		this.anioInicioAct = anioInicioAct;
	}
	private String ruc;
	private String correo;
	private String cargo;
	private String nombreContacto;
//	private String direccion;
	private String telefonoContacto;
	private String pais;
	private int anioInicio;
	
	private int anioInicioAct;
	private Long idEmpresa;
	//private String ciudad;
	
	
}
