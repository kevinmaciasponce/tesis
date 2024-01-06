package com.multiplos.cuentas.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancariaResponse {
	
	private String banco;
	private String tipoCuenta;
	private String numeroCuenta;
	private String titular;
	
}
