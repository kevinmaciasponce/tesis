package com.multiplos.cuentas.pojo.proyecto;

import lombok.Data;

@Data
public class ProyectoRequest {

	private String codigoProyecto;
	private Long idEmpresa;
	private String estadoActual;
	
}
