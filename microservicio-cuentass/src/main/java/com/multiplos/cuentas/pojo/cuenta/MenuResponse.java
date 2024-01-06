package com.multiplos.cuentas.pojo.cuenta;

import java.util.List;

import lombok.Data;

@Data
public class MenuResponse {

	private String nombre;
	private String descripcion;
	private String url;
    private int orden;
    private List<?> subMenu;
    private List<OperacionResponse> operacion;
    private String urlIcono;
}
