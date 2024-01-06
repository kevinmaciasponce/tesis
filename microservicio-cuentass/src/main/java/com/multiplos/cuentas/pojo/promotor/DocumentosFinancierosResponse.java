package com.multiplos.cuentas.pojo.promotor;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentosFinancierosResponse {
	private String impuestoRentaAnioAnterior;
	private String estadoFinancieroAnioAnterior;
	private String estadoFinancieroActuales;
	private String anexoCtsCobrar;
}
