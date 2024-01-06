package com.multiplos.cuentas.pojo.solicitud.filter;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FilterSolicitudRequest {

	//@NotNull(message = "identificación {NotNull}")
	private String identificacion;
	private String codProyecto;
	private Long numeroSolicitud;
	private Long idTipoCalificacion;
	private Long idActividad;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private LocalDate fecha;
	private String usuarioModificacion;
	//private String estado;
	
}
