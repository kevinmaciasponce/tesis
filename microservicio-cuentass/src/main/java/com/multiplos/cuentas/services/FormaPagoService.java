package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.FormaPago;

public interface FormaPagoService {

	List<FormaPago> findAll(String status);
	FormaPago consultaFormaPago(Long id);
	String save(FormaPago formaPago)throws Exception;
	String delete(Long id);
	
}
