package com.multiplos.cuentas.auspicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.auspicios.models.AuspicioTorneo;
import com.multiplos.cuentas.auspicios.models.Beneficiario;

public interface TorneosRepository  extends JpaRepository<AuspicioTorneo, Long> {

}
