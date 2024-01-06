package com.multiplos.cuentas.pojo.amortizacion;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FechaGenTblAmortRequest {

	@NotNull(message = "fechaGeneracion {NotNull}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private LocalDate fechaGeneracion;
	
	@NotNull(message = "codigoProyecto {NotNull}")
	private String codigoProyecto;
	
	@NotNull(message = "usuario {NotNull}")
	private String usuario;
}
