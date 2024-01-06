package com.multiplos.cuentas.services;

import org.json.JSONObject;

public interface ContificoService {
	
	
	Object consultarFacturaPorId(String id)throws Exception;
	Object crearFactura(JSONObject fact,String idProyecto)throws Exception;

}
