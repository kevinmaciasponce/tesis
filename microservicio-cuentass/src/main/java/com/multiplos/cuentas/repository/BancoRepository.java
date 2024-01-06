package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Banco;

public interface BancoRepository extends JpaRepository<Banco, Long> {
	
	@Query("select count(b) from Banco b where b.nombre=:banco "
			+ " and (:id is null or :id!= b.idBanco)"
			+ " and b.estado='A'")
	int existeBanco(String banco,Long id);
	
	@Query("select b from Banco b where (:status is null or b.estado=:status)")
	List<Banco> getAll(String status);
}
