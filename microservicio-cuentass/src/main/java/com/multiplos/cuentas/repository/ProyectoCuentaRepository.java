package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.ProyectoCuenta;

public interface ProyectoCuentaRepository extends JpaRepository<ProyectoCuenta, Long> {
	
	ProyectoCuenta findByIdProyectoCuenta(Long id);
	
	@Query("select c from ProyectoCuenta c where c.proyecto.idProyecto=?1 and c.estado='A'")
	ProyectoCuenta consultaCuentaPorProyecto(String idProyecto);
}
