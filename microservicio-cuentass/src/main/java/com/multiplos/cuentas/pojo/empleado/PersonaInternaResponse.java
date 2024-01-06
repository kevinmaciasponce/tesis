package com.multiplos.cuentas.pojo.empleado;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PersonaInternaResponse {
	private String idPersInterno;
	private String nombres;
	private String apellidos;
	private String estado;
	private String correo;
	private String iniciales;
	private String usuario;
	@JsonFormat(pattern = "YYYY-MM-DD")
	private LocalDateTime fechaCreacion;
	private String activo;

}
