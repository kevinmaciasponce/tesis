package com.multiplos.cuentas.pojo.promotor;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentosJuridicosResponse {
	private String escritura;
	private String estatutosVigentes;
	private String rucVigente;
	private String nombramientoRl;
	private String cedulaRl;
	private String nominaAccionista;
	private String identificacionesAccionista;
}
