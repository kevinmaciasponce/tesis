package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, String>{
	
	@Query("select count(c) from Cuenta c where c.persona.identificacion=?1 or upper(c.email)=?2 and c.estado='A'")
	int existeCuenta(String identificacion, String email);
	
	@Query("select c from Cuenta c where c.persona.identificacion=?1 and c.cuentaActiva='S' and c.estado='A'")
	Cuenta findByIdentificacion(String identificacion);
	
	@Query("select c from Cuenta c where c.email=?1 and c.estado='A'")
	Cuenta findByCuenta(String email);
	
	@Query("select c from Cuenta c where c.idCuenta=?1 and c.estado='A'")
	Cuenta findByCuentaIdCuenta(String idCuenta);
	
	@Query("select count(c) from Cuenta c where c.usuario=?1 and c.estado='A'")
	int findByCuentaUsuario(String usuario);
	
	@Query("select c from Cuenta c where c.usuario=?1 and c.estado='A'")
	Cuenta findByUsuario(String usuario);
	
	@Query("select c from Cuenta c where "
//			+ " c.rol.idRol=2 and "
			+ " c.estado='A'")
	List<Cuenta> consultaCuentasRegistradas();
	
}
