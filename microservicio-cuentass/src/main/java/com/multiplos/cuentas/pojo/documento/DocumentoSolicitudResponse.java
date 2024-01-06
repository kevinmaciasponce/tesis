package com.multiplos.cuentas.pojo.documento;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DocumentoSolicitudResponse {
	private String nombre;
	private String ruta;

}
