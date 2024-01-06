package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.Roles;
import com.multiplos.cuentas.models.RolesInternos;
import com.multiplos.cuentas.repository.CuentaInternoRepository;
import com.multiplos.cuentas.repository.CuentaRepository;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{
	
	private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	private CuentaRepository cuentaRepository;
	private CuentaInternoRepository cuentaUserInternoRepository;
	
	@Autowired
	public JpaUserDetailsService(CuentaRepository cuentaRepository, CuentaInternoRepository cuentaUserInternoRepository) {
		this.cuentaRepository = cuentaRepository;
		this.cuentaUserInternoRepository = cuentaUserInternoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String cuentaActiva;
		List<String> rol= new ArrayList<String>();;
		String usuario;
		String password;
		
		Cuenta cuenta = cuentaRepository.findByCuenta(username);
		if(cuenta != null) {
			//usuario inversionista o promotor
			cuentaActiva = cuenta.getCuentaActiva();
			for(Roles roles: cuenta.getRoles()) {
				rol.add(roles.getRol().getNombre());
			}
			//rol = cuenta.getRol().getNombre();
			usuario = cuenta.getEmail();
			password = cuenta.getClave();
		
			logger.error(cuenta.getCuentaActiva()+cuenta.getEmail()+cuenta.getClave());
		}else {
			//usuarios internos multiplo
			CuentaInterno cuentaUserInterno = cuentaUserInternoRepository.findByCuentaInterna(username);
			if(cuentaUserInterno == null) {
				//no exite el usuario(inversionista, promotor o interno)
				logger.error("Error en el Login: no existe el usuario '" + username + "' en el sistema");
		        throw new UsernameNotFoundException("Username: " + username + " no existe en el sistema");
	        }
			cuentaActiva = cuentaUserInterno.getCuentaActiva();
		//	rol = cuentaUserInterno.getRol().getNombre();
			for(RolesInternos roles: cuentaUserInterno.getRoles()) {
				rol.add(roles.getRol().getNombre());
			}
			usuario = cuentaUserInterno.getEmail();
			password = cuentaUserInterno.getClave();
		}
		
		if(cuentaActiva.contains("S")) {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			for(String rolAssig:rol) {
				 authorities.add(new SimpleGrantedAuthority(rolAssig));
			}
	       
			
	        if(authorities.isEmpty()) {
	        	logger.error("Error en el Login: Usuario '" + username + "' no tiene roles asignados");
	        	throw new UsernameNotFoundException("Error en el Login: usuario '" + username + "' no tiene roles asignados");
	        }
	        User us;
	        us= new User(usuario, password, true, true, true, true, authorities);
	        logger.error(us.getUsername()+us.getPassword());
	        return us;
	        
		}else {
			logger.error("Error en el Login: el usuario '" + username + "' no se encuentra activo");
        	throw new UsernameNotFoundException("Error en el Login: el usuario '" + username + "' no se encuentra activo");
		}
		
		/*
		Cuenta cuenta = cuentaRepository.findByCuenta(username);
		if(cuenta == null) {
			logger.error("Error en el Login: no existe el usuario '" + username + "' en el sistema");
	        throw new UsernameNotFoundException("Username: " + username + " no existe en el sistema");
        }
		
		if(cuenta.getCuentaActiva().contains("S")) {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	        authorities.add(new SimpleGrantedAuthority(cuenta.getRol().getNombre()));
			
	        if(authorities.isEmpty()) {
	        	logger.error("Error en el Login: Usuario '" + username + "' no tiene roles asignados");
	        	throw new UsernameNotFoundException("Error en el Login: usuario '" + username + "' no tiene roles asignados");
	        }
	        
	        return new User(cuenta.getEmail(), cuenta.getClave(), true, true, true, true, authorities);
		}else {
			logger.error("Error en el Login: el usuario '" + username + "' no se encuentra activo");
        	throw new UsernameNotFoundException("Error en el Login: el usuario '" + username + "' no se encuentra activo");
		}*/
	}

}
