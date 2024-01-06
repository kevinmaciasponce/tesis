package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PersonaTipoCuenta;

public interface PersonaTipoCuentaRepository extends JpaRepository<PersonaTipoCuenta, Long> {

	PersonaTipoCuenta findByIdPersCuenta(Long id);
}
