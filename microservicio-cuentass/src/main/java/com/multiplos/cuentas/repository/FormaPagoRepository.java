package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.models.FormaPago;

public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
	
	@Query("select f from FormaPago f where f.idFormaPago=?1 and f.estado='A'")
	FormaPago consultaFormaPago(Long idFormaPago);
	
	@Query("select count(f) from FormaPago f where (:idf is null or f.idFormaPago !=:idf)"
			+ " and  f.descripcion=:descripcion  "
			+ " and f.estado='A'")
	int existeFormaPago(String descripcion, Long idf);
	
	@Query("select c from FormaPago c where (:status is null or c.estado=:status)")
	List<FormaPago> getAll(String status);
	
	
}
