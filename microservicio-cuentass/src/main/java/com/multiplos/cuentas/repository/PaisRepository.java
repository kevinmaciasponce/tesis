package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Pais;

public interface PaisRepository extends JpaRepository<Pais, Long> {

	@Query("select count(p) from Pais p where (p.pais=:pais or p.gentilicio=:gentilicio) "
			+ " and (:id is null or p.idNacionalidad!=:id) ")
	int existePais(String pais, String gentilicio, Long id);
	
	@Query("select p.pais from Pais p where p.iso=?1 and p.estado='A'")
	String consultaPaisPorAbrebiatura(String iso);
	
	@Query("select p from Pais p where (:status is null or p.estado=:status)")
	List<Pais> getAll(String status);
			

}
