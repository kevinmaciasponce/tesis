package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.DatosInversion;

public interface DatosInversionService {
	DatosInversion findByIdDato(Long id);
	DatosInversion saveDatoInversion(DatosInversion datoInversion);
	void updateDatosTablaAmortizacion(boolean tablaAmortizacion, Long idDato);
	void updateDatosFormulario(boolean formulario, Long idDato);
	void updateDatosDocumentacion(boolean documentacion, Long idDato);
	void updateDatosPago(boolean pago, Long idDato);
}
