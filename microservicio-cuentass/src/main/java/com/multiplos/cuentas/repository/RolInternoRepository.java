package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.pojo.empleado.RolInternoResponse;

public interface RolInternoRepository extends JpaRepository<RolInt, Long> {

	@Query("select r from RolInt r where r.nombre=?1 and r.estado='A'")
	RolInt consultaRol(String rol);
	
	@Query("select new com.multiplos.cuentas.pojo.empleado.RolInternoResponse(r.idRol,r.nombre) from RolInt r")
	List<RolInternoResponse>consultarRolesInternos();
	
	@Query("select new com.multiplos.cuentas.pojo.empleado.RolInternoResponse(r.rol.idRol, r.rol.nombre) from RolesInternos r "
			+ " where r.cuentaInterna.idCuenta=:idCuenta")
	List<RolInternoResponse>consultarRolesInternosByempleado(String idCuenta);
}
