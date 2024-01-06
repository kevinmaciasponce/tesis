package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
	
	@Query("select count(c) from Ciudad c where c.ciudad=:ciudad "
			+ " and ( :id is null or :id!=c.idCiudad)"
			+ " and c.estado='A'")
	int existeCiudad(String ciudad,Long id);
	@Query("select c from Ciudad c where c.pais.idNacionalidad=?1")
	List<Ciudad> findByIdPais(Long idPais);
	
	@Query("select c from Ciudad c where (:status is null or c.estado=:status)")
	List<Ciudad> all(String status);
	

}
