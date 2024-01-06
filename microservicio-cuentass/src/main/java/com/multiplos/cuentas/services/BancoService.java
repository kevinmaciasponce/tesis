package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Banco;

public interface BancoService {

	List<Banco> findAll(String status);
	Banco findById(Long id);
	String save(Banco banco)throws Exception;
	String deleteBanco(Long id);
}
