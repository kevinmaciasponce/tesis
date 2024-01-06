package com.multiplos.cuentas.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResponseBoolean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Boolean mensaje;
	
	public ResponseBoolean(Boolean mensaje) {
		this.mensaje = mensaje;
	}

}
