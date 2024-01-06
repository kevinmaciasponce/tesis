package com.multiplos.cuentas.pojo.persona;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class DocIdentificacionResponse {
	private String nombre;
	private String ruta;
	private String nombre_post;
	private String ruta_post;
}
