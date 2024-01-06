package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.HistorialConciliacion;



public interface HistorialConciliacionRepository extends  JpaRepository<HistorialConciliacion, Long> {
	
	

}