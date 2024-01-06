package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.InfoEmail;

public interface InfoEmailRepository extends JpaRepository<InfoEmail, Long> {
	
	@Modifying
	@Query("update InfoEmail e set e.enviado = ?1 where e.idEmail = ?2")
	void updateEmailEnviado(String enviado, Long idEmail);

}
