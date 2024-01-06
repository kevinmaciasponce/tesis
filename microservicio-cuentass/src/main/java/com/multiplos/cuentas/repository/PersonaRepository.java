package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.PersonaIds;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersInfoAdicionalResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;

public interface PersonaRepository extends JpaRepository<Persona, PersonaIds>{
	
	@Query("select p from Persona p where p.cuenta.idCuenta=?1")
	Persona consultaPersonas(String idCuenta);
	
	
	
	@Query("select p from Persona p where p.identificacion=?1 and p.estado='A'")
	Persona findById(String identificacion);
	
	@Query("select new com.multiplos.cuentas.pojo.persona.FilterPersonaResponse(CONCAT(p.nombres,' ',p.apellidos), p.identificacion)"
			+ " from Persona p where p.estado='A' ")
	List<?> consultaFilterPersona();
	
	@Query("select p.identificacion,p.tipoCliente, p.tipoPersona, p.tipoIdentificacion,p.nacionalidad,p.nombres,p.apellidos,"
			+ " p.fechaNacimiento, p.numeroCelular,p.razonSocial, p.nombreContacto, p.cargoContacto, p.emailContacto, p.anioInicioActividad, p.cuenta.email, p.cuenta.usuario, p.cuenta.tipoContacto, p.cuenta.usuarioContacto "
			+ " from Persona p where p.identificacion=?1 and p.estado='A'")
	PersonaResponse consultaPersonaResponse(String identificacion);
	
	@Query("select p.estadoCivil"
			+ " from PersonaInfoAdicional p where p.persona.identificacion=?1 and p.estado='A'")
	PersInfoAdicionalResponse consultaInfoAdicionalResponse(String identificacion);
	
	@Query("select new com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse (p.personaDocumentos.nombre, p.personaDocumentos.ruta, p.personaDocumentos.nombrepost,  p.personaDocumentos.rutapost)"
			+ " from PersonaInfoAdicional p where p.persona.identificacion=?1 and p.estado='A'")
	DocIdentificacionResponse consultaDocIdentificacion(String identificacion);

	@Query("select count(p) from Persona p where p.identificacion=?1 and p.estado='A'")
	int existePersona(String identificacion);
}
