package com.multiplos.cuentas.pojo.promotor;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmpresaRequest {

//	@NotNull(message = "idPromo no puede null")
	private Long id;
	private String cuenta;

	
}
