package com.multiplos.cuentas.auspicios.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

public class AuspicioException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 public AuspicioException(String mensaje) {
	      super(mensaje);
	    }
	

}
