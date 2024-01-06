package com.multiplos.cuentas.pojo.solicitud.responseManagerOper;

import java.math.BigDecimal;
import java.util.List;

import com.multiplos.cuentas.pojo.solicitud.filter.inversionResponse;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SolicitudPapResponse {
	@NonNull
	private Long numeroSolicitud;
	@NonNull
	private String identificacion;
	@NonNull
	private String nombreEmpresa;
	@NonNull
	private BigDecimal inversion;
	@SuppressWarnings("deprecation")
	@NonNull
	private int plazo;
	@NonNull
	private String estado;
	@NonNull
	private String codProyecto;
	@NonNull
	private BigDecimal montoProyecto;
	private List<?> listaTransacciones; 
}
