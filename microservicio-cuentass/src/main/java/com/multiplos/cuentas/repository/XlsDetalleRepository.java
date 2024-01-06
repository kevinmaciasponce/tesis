package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.multiplos.cuentas.models.ConciliaXlsDetalle;

public interface XlsDetalleRepository extends JpaRepository<ConciliaXlsDetalle, Long>{

}