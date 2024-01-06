package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Parametro;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {
	
	List<Parametro> findByParametro(String parametro);
	
	@Query("select p from Parametro p where p.codParametro=:codParametro and p.estado='A'")
	Parametro findByParametroCodParametro(String codParametro);
	
	@Query("select p.descripcion from Parametro p where p.valor=?1 and p.estado='A'")
	String consultaParametroDescripcion(String valor);//
	
	
}
