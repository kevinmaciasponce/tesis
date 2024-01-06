package com.multiplos.cuentas.pojo.solicitud.filter;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multiplos.cuentas.models.TipoEstado;

import lombok.Data;

@Data
public class FilterIntSolicitudRequest {
	
	private String identificacion;
	private Long numeroSolicitud;
	private String codProyecto;
	private Long idTipoCalificacion;
	private Long idActividad;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private LocalDate fecha;
}
