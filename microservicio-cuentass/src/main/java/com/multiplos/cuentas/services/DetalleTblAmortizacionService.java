package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.DetalleAmortizacion;

public interface DetalleTblAmortizacionService {
	
	DetalleAmortizacion save(DetalleAmortizacion detTblAmortizacion);
	List<DetalleAmortizacion> consultaDetalleTblAmortizacion(Long idTblAmortizacion);
	String updateDetalleAmortizacion(Long idTblAmortizacion);
}
