package com.multiplos.cuentas.pojo.plantilla;

import java.util.List;

import lombok.Data;

@Data
public class PlantillaDocumentos {

	private String titulo;
	private List<String> items;
	private String estado;
	
}
