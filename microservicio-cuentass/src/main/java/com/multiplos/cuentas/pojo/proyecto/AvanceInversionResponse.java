package com.multiplos.cuentas.pojo.proyecto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvanceInversionResponse {
	
	private String codigoOperacion;
	private String valor;
	public AvanceInversionResponse(String valor, String porcentaje) {
		super();
		this.valor = valor;
		this.porcentaje = porcentaje;
	}
	private String porcentaje;

}
