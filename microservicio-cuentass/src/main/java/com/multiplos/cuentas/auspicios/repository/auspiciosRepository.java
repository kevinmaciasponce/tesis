package com.multiplos.cuentas.auspicios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.auspicios.models.Auspicio;
import com.multiplos.cuentas.auspicios.models.AuspicioValoracion;
import com.multiplos.cuentas.auspicios.models.Beneficiario;
import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Deportista;
import com.multiplos.cuentas.models.Solicitud;

public interface auspiciosRepository extends JpaRepository<Beneficiario, String> {

	@Query(" select v from AuspicioValoracion v where v.beneficiario.id=:identificacion and v.activo=:status")
	AuspicioValoracion findValidationByActive(String identificacion, Boolean status);
	
	@Query(" select v from AuspicioValoracion v where v.id=:id ")
	AuspicioValoracion findValidationById(Long id);
	
	@Query(" select v from AuspicioValoracion v where v.beneficiario.id=:identificacion and v.activo=:status")
	List<AuspicioValoracion> findValidationList(String identificacion, Boolean status);
	
	@Query(" select v from Auspicio v where v.beneficiario.id=:identificacion and v.activo=:status")
	Auspicio findAuspicioActual(String identificacion, Boolean status);
	
	@Query(" select v from Auspicio v where v.beneficiario.id=:identificacion and v.estado.idEstado=:status")
	Auspicio findAuspicioByEstado(String identificacion, String status);
	
	
	@Query(" select v from Auspicio v where v.id=:id ")
	Auspicio findAuspicioById(Long id);
	
	@Query(" select v.activo from Beneficiario v where v.id=:identificacion ")
	Boolean isBeneficiario(String identificacion);
}
