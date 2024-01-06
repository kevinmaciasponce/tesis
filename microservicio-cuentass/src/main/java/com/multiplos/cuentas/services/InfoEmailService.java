package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.InfoEmail;

public interface InfoEmailService {
	
	List<InfoEmail> consultaBaseEmails();
	void actualizaEmailEnviado(String enviado, Long idEmail);

}
