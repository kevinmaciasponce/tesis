package com.multiplos.cuentas.auspicios.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioFilter {

@NotNull(message = "Beneficiario no puede estar vacio")	
private String identificacion;
@NotNull(message = "representante no puede estar vacio")	
private String representante;
//@NotNull(message = "disciplina no puede estar vacio")	
//private Long disciplina;
//@NotNull(message = "estado no puede estar vacio")	
//private String estadoAus;
	
}
