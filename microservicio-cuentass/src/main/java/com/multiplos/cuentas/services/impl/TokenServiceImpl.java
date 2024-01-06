package com.multiplos.cuentas.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Token;
import com.multiplos.cuentas.repository.TokenRepository;
import com.multiplos.cuentas.services.EmailSenderService;
import com.multiplos.cuentas.services.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	private TokenRepository tokenRepository;
	private EmailSenderService emailSenderService;
	
	private static final Logger LOG = LoggerFactory.getLogger(TokenServiceImpl.class);
	
	@Autowired
	public TokenServiceImpl(TokenRepository tokenRepository, EmailSenderService emailSenderService) {
		this.tokenRepository = tokenRepository;
		this.emailSenderService = emailSenderService;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Token findByToken(String token) {
		return tokenRepository.findByToken(token);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Token findByCuenta(Cuenta cuenta) {
		return tokenRepository.findByCuenta(cuenta);
	}
	
	@Override
	@Transactional
	public String generaToken(Token token) {
		try {
			tokenRepository.save(token);
		}catch(DataAccessException e) {
			LOG.error("generaToken: Error generar el token: "+e.getMessage());
			return "No se pudo generar el token";
		}
		return "ok";
	}
	
	
	
	

}
