package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.TipoSolicitud;

public interface TipoTransaccionRepository extends JpaRepository<TipoSolicitud, Long> {

}
