package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.ProyectoCuenta;
import com.multiplos.cuentas.pojo.proyecto.CuentaHabilResponse;

public interface ProyectoCuentaService {
	
	ProyectoCuenta findByIdProyectoCuenta(Long id);
	CuentaHabilResponse consultaCuentaPorProyecto(String idProyecto) throws Exception;
}
