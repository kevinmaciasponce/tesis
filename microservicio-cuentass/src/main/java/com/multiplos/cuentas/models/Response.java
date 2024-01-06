package com.multiplos.cuentas.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Object mensaje;
	
	public Response(Object mensaje) {
		this.mensaje = mensaje;
	}

}
