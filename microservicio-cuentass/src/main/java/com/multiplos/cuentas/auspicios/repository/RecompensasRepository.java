package com.multiplos.cuentas.auspicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.auspicios.models.AuspicioRecompensa;

public interface RecompensasRepository extends  JpaRepository<AuspicioRecompensa,Long> {

}
