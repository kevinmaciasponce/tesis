package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.PersonaInterna;
import com.multiplos.cuentas.pojo.empleado.PersonaInternaResponse;


public interface PersonaInternaRepository extends CrudRepository<PersonaInterna, String>{
	
	
	@Query("select new com.multiplos.cuentas.pojo.empleado.PersonaInternaResponse("
			+ "e.idPersInterno, e.nombres, e.apellidos, e.estado,e.cuenta.email,e.iniciales,e.cuenta.idCuenta,e.fechaCreacion,e.cuenta.cuentaActiva) "
			+ "from PersonaInterna e where "
			+ "(:id is null or :id = e.idPersInterno)")
	List<PersonaInternaResponse>findByFilter(String id);
	
	@Query("select e from PersonaInterna e where (e.idJefe=?1 ) and e.estado='A'")
	List<PersonaInterna> findByAnalistasOperacionesPorJefe(Long idJefe);
	
	@Query("select e from PersonaInterna e where e.cuenta.idCuenta=?1 and e.estado='A'")
	PersonaInterna consultaEmpleado(String usuario);
	
	@Query("select e.cuenta from PersonaInterna e where e.cuenta.idCuenta=?1 and e.cuenta.estado='A'")
	CuentaInterno consultacuentaEmpleado(String usuario);
	
	
	@Query("select e.email from CuentaInterno e where e.email=?1")
	String isMail(String mail) ;
	
	
//	@Query("select e from PersonaInterna e where e.cuenta.rol.idRol=?1 and e.cuenta.estado='A'")
//	List<PersonaInterna> consultaEmpleadosPorRol(Long idRol);
}
