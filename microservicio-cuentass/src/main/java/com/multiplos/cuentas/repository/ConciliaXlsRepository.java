package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.ConciliacionXls;

public interface ConciliaXlsRepository extends JpaRepository<ConciliacionXls, Long> {

	//Conciliacsvcabecera findTopByOrderByIdDesc();
	
	@Query("select MAX(s.id) FROM ConciliacionXls s" )
	Long findTopId();
	
	@Query("select s FROM ConciliacionXls s where s.estado ='A'")
	ConciliacionXls findActive();
	
	
	
	/*@Query("select c FROM Conciliacsvcabecera c  where c.id=:?1" )
	Conciliacsvcabecera findTopByOrderByIdDesc(Long id);*/
	
	
}