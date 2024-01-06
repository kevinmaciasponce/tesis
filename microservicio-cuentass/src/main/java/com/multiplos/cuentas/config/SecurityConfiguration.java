package com.multiplos.cuentas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.multiplos.cuentas.auth.filter.JWTAuthenticationFilter;
import com.multiplos.cuentas.auth.filter.JWTAuthorizationFilter;
import com.multiplos.cuentas.auth.service.JWTService;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.impl.JpaUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private JpaUserDetailsService userDetailsService;
	@Autowired
	private JWTService jwtService;
	
	@Autowired private CuentaService cuentaService;
	
	/*@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/**").permitAll()
		
		.antMatchers("/multiplo/validacion-email").permitAll()
    	.antMatchers("/multiplo/confirmacion-email").permitAll()
		.antMatchers("/multiplo/confirm").permitAll()
		
		.anyRequest().authenticated()
		.and().csrf().disable();
	}*/
	
	@Bean
	public BCryptPasswordEncoder bcryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.cors()
		.and()
		.csrf().disable()
		.authorizeRequests()
		
		
		.antMatchers("/img/**").permitAll()
		//.antMatchers("/multiplo/validacion-email").permitAll()//solo admin
    	.antMatchers("/multiplo/confirmacion-email/**").permitAll()
		.antMatchers("/multiplo/confirm-account").permitAll()
		
		.antMatchers("/multiplo/cuentas/**").permitAll()//validaEmail,creaCuenta
		.antMatchers(HttpMethod.GET,"/multiplo/parametros/**",
									"/multiplo/paises/**",
									"/multiplo/ciudades/**",
									"/multiplo/calificaciones/**",
									"/multiplo/sector/**",
									"/multiplo/formaPagos/**").permitAll()
		.antMatchers(HttpMethod.GET,"/multiplo/proyectos/**").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/proyectos/**").permitAll()
		.antMatchers(HttpMethod.GET,"/multiplo/empleados/**").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/amortizacion/simulador").permitAll()
		.antMatchers(HttpMethod.GET,"/multiplo/tipoEstados/**").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/persona").hasAnyAuthority("INVERSIONISTA","ANALISTAOPER","GERENTEGENERAL","GERENTEOPER")
		.antMatchers("/multiplo/solicitud/formularios/**").permitAll()
		.antMatchers("/multiplo/amortizacion/**").hasAnyAuthority("INVERSIONISTA","ANALISTAOPER","GERENTEGENERAL","GERENTEOPER")
		.antMatchers("/multiplo/proyectos/**").hasAnyAuthority("INVERSIONISTA")
		
		
		///////////////
		.antMatchers("/multiplo/public/**").permitAll()
	
		.antMatchers("/multiplo/admin/**").permitAll()
		
		.antMatchers("/multiplo/investor/**").hasAnyAuthority("INVERSIONISTA")
		
		.antMatchers("/multiplo/analista/**").hasAnyAuthority("ANALISTAOPER")
		
		.antMatchers("/multiplo/manager/**").hasAnyAuthority("GERENTEGENERAL")
		
		.antMatchers("/multiplo/managerOper/**").hasAnyAuthority("GERENTEOPER")
		
		.antMatchers("/multiplo/beneficiario/**").hasAnyAuthority("BENEFICIARIO")
		
		.antMatchers("/multiplo/representante/**").permitAll()
		
		.antMatchers("/multiplo/analistaAuspicio/**").permitAll()
		
		.antMatchers("/multiplo/promotor/**").permitAll()
		
		.antMatchers("/multiplo/prubas/**").permitAll()
		
		.antMatchers("/multiplo/consultas/**").hasAnyAuthority("INVERSIONISTA","ANALISTAOPER","GERENTEGENERAL","GERENTEOPER","REPRESENTANTE")
		
		//.antMatchers("/multiplo/consultas/int/**")("ANALISTAOPER","GERENTEGENERAL","GERENTEOPER")
		
		///////////////////
		
		.antMatchers(HttpMethod.POST,"/multiplo/solicitud/consultas/**").hasAnyAuthority("INVERSIONISTA","ANALISTAOPER","GERENTEGENERAL","GERENTEOPER")
		.antMatchers(HttpMethod.POST,"/multiplo/solicitud/inversiones/filter/**").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/solicitud/manager/filter/**").hasAnyAuthority("GERENTEGENERAL","GERENTEOPER")
		.antMatchers(HttpMethod.POST,"/multiplo/solicitud/int/documentos/contratos").hasAnyAuthority("ANALISTAOPER")
		.antMatchers(HttpMethod.POST,"/multiplo/solicitud/int/proyectos/*").hasAnyAuthority("ANALISTAOPER")
		.antMatchers("/multiplo/solicitud/formularios/documentos/deposito").hasAnyAuthority("ANALISTAOPER")
		
		

		.antMatchers("/multiplo/solicitud/inversiones/consultas/int/**").hasAnyAuthority("ANALISTAOPER","GERENTEGENERAL")
		.antMatchers("/multiplo/solicitud/inversiones/procesos/int/**").hasAnyAuthority("ANALISTAOPER","GERENTEGENERAL")
		//.antMatchers("/multiplo/conciliaciones/**","/multiplo/generacion/**").hasAnyAuthority("ANALISTAOPER","GERENTEGENERAL")
		.antMatchers("/multiplo/inversiones/int/**").hasAnyAuthority("GERENTEGENERAL")

		.antMatchers(HttpMethod.POST,"/multiplo/formcontact").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/proyectos/update").permitAll()
		//.antMatchers(HttpMethod.GET,"/multiplo/rol/**").permitAll()//solo pruebas
		//.antMatchers(HttpMethod.GET,"/multiplo/menu").permitAll()//solo pruebas
		//.antMatchers(HttpMethod.POST,"/multiplo/solicitud/inversiones/consultas/proyectoPorEstados/**").permitAll()//solo pruebas
		.antMatchers(HttpMethod.POST,"/multiplo/conciliaciones/**").hasAnyAuthority("ANALISTAOPER","GERENTEGENERAL")//solo pruebas
		//.antMatchers("/multiplo/inversiones/int/**").permitAll()//solo pruebas

		.antMatchers("/multiplo/conciliaciones/conciliarDatos").hasAnyAuthority("ANALISTAOPER","GERENTEGENERAL")//solo pruebas

		.antMatchers(HttpMethod.GET,"/multiplo/loquesea").permitAll()
		.antMatchers(HttpMethod.POST,"/multiplo/loquesea2").permitAll()
		.anyRequest().authenticated()
		.and()
		.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService, cuentaService))
		.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception
	{	
		build.userDetailsService(userDetailsService);
		//.passwordEncoder(passwordEncoder);
	}
	
	
}