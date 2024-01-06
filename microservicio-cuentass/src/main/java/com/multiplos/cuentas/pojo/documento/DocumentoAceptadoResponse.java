package com.multiplos.cuentas.pojo.documento;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DocumentoAceptadoResponse {

	private String nombre;
	@JsonFormat(pattern="dd-MM-yyyy HH:mm ")
	private LocalDateTime fecha;
	private String estado;
	private int version;
	private String url;
	
}
