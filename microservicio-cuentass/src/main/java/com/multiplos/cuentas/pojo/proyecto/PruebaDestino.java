package com.multiplos.cuentas.pojo.proyecto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PruebaDestino {

	private String DestinoFinanciamiento;
	private String solvencia;
	
	
	
	public PruebaDestino(String destinoFinanciamiento, String solvencia) {
		super();
		DestinoFinanciamiento = destinoFinanciamiento;
		this.solvencia = solvencia;
	}
	
	
}
