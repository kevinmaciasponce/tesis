package com.multiplos.cuentas.pojo.formulario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDatosIngresadoResponse {

	private boolean tablaAmortizacion;
	private boolean formulario;
	private boolean documentacion;
	private boolean pago;
}
