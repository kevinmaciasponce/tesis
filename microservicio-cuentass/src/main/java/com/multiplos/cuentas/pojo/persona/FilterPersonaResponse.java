package com.multiplos.cuentas.pojo.persona;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterPersonaResponse {

	private String nombres;
	private String identificacion;
}
