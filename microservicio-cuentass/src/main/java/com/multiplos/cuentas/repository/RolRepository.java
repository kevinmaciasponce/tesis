package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

	@Query("select r from Rol r where r.nombre=?1 and r.estado='A'")
	Rol consultaRol(String rol);

}
