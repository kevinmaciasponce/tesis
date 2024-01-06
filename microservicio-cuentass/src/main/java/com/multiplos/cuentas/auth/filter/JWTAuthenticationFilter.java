package com.multiplos.cuentas.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.auth.service.JWTService;
import com.multiplos.cuentas.auth.service.JWTServiceImpl;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Token;
import com.multiplos.cuentas.pojo.cuenta.CuentaRolResponse;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.impl.JpaUserDetailsService;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	private JWTService jwtService;
	
	private CuentaService cuentaService;
	private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService,
			CuentaService cuentaService ) {
		
		this.authenticationManager = authenticationManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/multiplo/login", "POST"));
		
		this.jwtService = jwtService;
		this.cuentaService = cuentaService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		logger.error(username+password);
		if(username != null && password !=null) {
		} else {
			Cuenta user = null;
			try {
				
				user = new ObjectMapper().readValue(request.getInputStream(), Cuenta.class);
			
				username = user.getEmail();
				password = user.getClave();
				
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		username = username.trim();
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String token = jwtService.create(authResult);
		
		response.addHeader(JWTServiceImpl.HEADER_STRING, JWTServiceImpl.TOKEN_PREFIX + token);
				
		User user = (User) authResult.getPrincipal();
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("token", token);
		
		String rolAsignado = null;
		for(GrantedAuthority r : user.getAuthorities()) {
			rolAsignado = r.getAuthority();
			
			
		}
		CuentaRolResponse respCuentaRol = new CuentaRolResponse();
		respCuentaRol = cuentaService.consultaCuentaRol(rolAsignado, user.getUsername());
		
		body.put("user", respCuentaRol);
		//body.put("mensaje", String.format("Inicio de sesión con éxito!"));
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
	}
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException{
		
		Map<String, Object> body = new HashMap<String, Object>();
		
		body.put("mensaje", "Error de autenticación: usuario o contraseña incorrecto");
		body.put("error", failed.getMessage());
				
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}
	
	

}
