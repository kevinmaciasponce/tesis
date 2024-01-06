package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.RangoPago;

public interface RangPagoRepository extends JpaRepository<RangoPago, Long> {

}
