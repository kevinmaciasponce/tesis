package com.multiplos.cuentas.pojo.login;

import java.util.List;

import com.multiplos.cuentas.models.MenuOperacion;
import com.multiplos.cuentas.pojo.cuenta.MenuResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolesResponse {

	private String nombre;
	private String ruta;
	private List<MenuResponse> menu;
	//private List<?> menuOperaciones;
	
}
