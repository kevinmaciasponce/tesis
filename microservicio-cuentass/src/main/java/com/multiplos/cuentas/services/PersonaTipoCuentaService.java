package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.PersonaTipoCuenta;

public interface PersonaTipoCuentaService {
	PersonaTipoCuenta findByIdPersCuenta(Long id);
	PersonaTipoCuenta saveTipoCuenta(PersonaTipoCuenta cuenta);
}
