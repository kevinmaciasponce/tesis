package com.multiplos.cuentas.pojo.proyecto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProyectoRutaResponse {
	private String name;
	private String ruta;
}
