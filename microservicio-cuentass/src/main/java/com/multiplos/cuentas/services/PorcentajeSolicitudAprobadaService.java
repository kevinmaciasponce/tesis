package com.multiplos.cuentas.services;

import java.time.LocalDate;
import java.util.List;

import com.multiplos.cuentas.models.PorcentajeSolicitudAprobada;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoProyecto;
import com.multiplos.cuentas.pojo.proyecto.PorcSolAprobadaRequest;

public interface PorcentajeSolicitudAprobadaService {

	String guardaPorcentajeSolAprobada(PorcSolAprobadaRequest porcSolAprobada) throws Exception;
	PorcentajeSolicitudAprobada consultaPorcSolAprobado(String codigoProyecto);
	List<DatoProyecto> consultaProyectosAprobadoTransferirFondo(LocalDate fechaAprobacion);
}
