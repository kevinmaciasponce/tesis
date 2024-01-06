package com.multiplos.cuentas.pojo.formulario;

import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;

import lombok.Data;

@Data
public class FormDomicilioResponse {
	private PaisResponse pais;
	private CiudadResponse ciudad;
	private String direccion;
	private String numeroDomicilio;
	private String sector;	
}
