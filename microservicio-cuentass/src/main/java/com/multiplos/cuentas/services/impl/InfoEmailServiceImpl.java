package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.InfoEmail;
import com.multiplos.cuentas.repository.InfoEmailRepository;
import com.multiplos.cuentas.services.InfoEmailService;

@Service
public class InfoEmailServiceImpl implements InfoEmailService {

	private InfoEmailRepository repository;
	
	@Autowired
	public InfoEmailServiceImpl(InfoEmailRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfoEmail> consultaBaseEmails() {
		List<InfoEmail> infoEmails = new ArrayList<>();
		infoEmails = repository.findAll().stream()
				      .filter(es -> es.getEstado().contains("A"))
				      .collect(Collectors.toList());
		return infoEmails;
	}

	@Override
	@Transactional
	public void actualizaEmailEnviado(String enviado, Long idEmail) {
		repository.updateEmailEnviado(enviado, idEmail);		
	}

}
