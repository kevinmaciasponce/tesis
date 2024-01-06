package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.BitacoraProceso;

public interface BitacoraProcesoService {

	void guardaBitacora(BitacoraProceso bitacora);
	BitacoraProceso consultaBitacoraPorProcesoAndTipo(String proceso, String tipo);
	void guardaBitacoraAll(List<BitacoraProceso> bitacora);
	
}
