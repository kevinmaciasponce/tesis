package com.multiplos.cuentas.pojo.cuenta;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.DocumentosJuridicosPromotor;
import com.multiplos.cuentas.models.Persona;
import com.sendgrid.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuentaRequest {
	
	@NotNull(message = "tipo cliente {NotNull}")
	private String tipoCliente;
	
	@NotNull(message = "tipo persona {NotNull}")
	private String tipoPersona;
	
	@NotNull(message = "tipo identificacion {NotNull}")
	private String tipoIdentificacion;
	
	@NotNull(message = "identificación {NotNull}")
	@Size(min = 10, max = 13, message = "{Size.identificacion}")
	@Pattern(regexp="[0-9]+",message = "identificación {Pattern.numeros}")
	private String identificacion;
	
	@NotNull(message = "nacionalidad {NotNull}")
	private String nacionalidad;
	
	
	@Size(min = 2, max = 50, message = "nombres {Size.nombreApellido}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóúÑñ ]+",message = "nombres {Pattern.letras}")
	private String nombres;
	
	
	@Size(min = 2, max = 50, message = "apellidos {Size.nombreApellido}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóúÑñ ]+",message = "apellidos {Pattern.letras}")
	private String apellidos;
	
	
	private Date fechaNacimiento;
	
	@NotNull(message = "correo {NotNull}")
	@Email(message = "{Email}")
	private String email;
	
	@NotNull(message = "celular {NotNull}")
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.CuentaRequest.numeroCelular}")
	private String numeroCelular;
	
	@NotNull(message = "{NotNull.tipoContacto}")
	private String tipoContacto;
	
	@NotNull(message = "usuario contacto no debe estar vacio")
	@Size(min = 0, max = 50, message = "usuario contacto debe estar entre 0 y 50 caracteres")
	private String usuarioContacto;
	
	@Size(min = 5, max = 100, message = "{Razón Social {Size.longitud}")
	@Pattern(regexp="[A-Za-zÁÉÍÓÚáéíóúÑñ .]+",message = "Razón Social {Pattern.letras}")
	private String razonSocial;
	
	@Size(min = 5, max = 100, message = "Nombre del contacto {Size.longitud}")
	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóúÑñ ]+",message = "Nombre del contacto {Pattern.letras}")
	private String nombreContacto;
	
	@Size(min = 5, max = 50, message = "{cargoContacto}")
	@Pattern(regexp="[A-Za-z_]+",message = "Cargo del contacto {Pattern.letras}")
	private String cargoContacto;
	@Email(message = "{Email}")
	private String emailContacto;

	private int anioInicioActividad;

	@NotNull(message = "acepta politica {NotNull}")
	private String aceptaPoliticaPrivacidad;
	@NotNull(message = "acepta terminos {NotNull}")
	private String aceptaTerminoUso;
	@NotNull(message = "acepta recibir información {NotNull}")
	private String aceptaRecibirInformacion;
	
	private String usuarioCreacion;
	@NotNull(message = "Contraseña {NotNull}")
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\\d$@$.,#/<>;:*!%?&].{6,12}$", message="{clave}")
	private String clave;
	

	
	
	
//	
//	private CuentaRequest (CuentaRequest request) {
//		Persona persona = new Persona();
//		persona.setAnioInicioActividad(request.anioInicioActividad);
//		persona.setApellidos(request.apellidos);
//		persona.setCargoContacto(request.cargoContacto);
//		persona.setEmailContacto(request.emailContacto);
//		persona.setFechaNacimiento(request.fechaNacimiento);
//		persona.setIdentificacion(request.identificacion);
//		persona.setNacionalidad(request.nacionalidad);
//		persona.setNombreContacto(request.nombreContacto);
//		persona.setNombres(request.nombres);
//		persona.setNumeroCelular(request.numeroCelular);
//		persona.setRazonSocial(request.razonSocial);
//		persona.setTipoCliente(request.tipoCliente);
//		persona.setTipoIdentificacion(request.tipoIdentificacion);
//		persona.setTipoPersona(request.tipoPersona);
//		persona.setUsuarioCreacion(request.identificacion);
//		Object pers = persona;
//		return pers;
//	}
//	
	 public  Persona getPersonaJur(Cuenta cuenta) {
	 Persona persona = new Persona();
		persona.setAnioInicioActividad(anioInicioActividad);
		persona.setApellidos("");
		persona.setNombres("");
		persona.setCargoContacto(cargoContacto);
		persona.setEmailContacto(emailContacto);
		persona.setNombreContacto(nombreContacto);
		persona.setRazonSocial(razonSocial);
		persona.setFechaNacimiento(null);
		persona.setIdentificacion(identificacion);
		persona.setNacionalidad(nacionalidad);
		persona.setNumeroCelular(numeroCelular);
		persona.setTipoCliente(tipoCliente);
		persona.setTipoIdentificacion(tipoIdentificacion);
		persona.setTipoPersona(tipoPersona);
		persona.setUsuarioCreacion(usuarioCreacion);
		persona.setCuenta(cuenta);
			return persona; 
		 }
	 
	 public  Persona getPersonaNat(Cuenta cuenta) {
		 Persona persona = new Persona();
			persona.setAnioInicioActividad(0);
			persona.setApellidos(apellidos);
			persona.setNombres(nombres);
			persona.setCargoContacto("");
			persona.setEmailContacto("");
			persona.setRazonSocial("");
			persona.setNombreContacto("");
			persona.setFechaNacimiento(fechaNacimiento);
			persona.setIdentificacion(identificacion);
			persona.setNacionalidad(nacionalidad);
			persona.setNumeroCelular(numeroCelular);			
			persona.setTipoCliente(tipoCliente);
			persona.setTipoIdentificacion(tipoIdentificacion);
			persona.setTipoPersona(tipoPersona);
			persona.setUsuarioCreacion(usuarioCreacion);
			persona.setCuenta(cuenta);
				return persona; 
			 }
	 public  Persona getPersona(Cuenta cuenta) {
		 Persona persona = new Persona();
			persona.setAnioInicioActividad(anioInicioActividad);
			persona.setApellidos(apellidos);
			persona.setNombres(nombres);
			persona.setCargoContacto(cargoContacto);
			persona.setEmailContacto(emailContacto);
			persona.setRazonSocial(razonSocial);
			persona.setNombreContacto(nombreContacto);
			persona.setFechaNacimiento(fechaNacimiento);
			persona.setIdentificacion(identificacion);
			persona.setNacionalidad(nacionalidad);
			persona.setNumeroCelular(numeroCelular);			
			persona.setTipoCliente(tipoCliente);
			persona.setTipoIdentificacion(tipoIdentificacion);
			persona.setTipoPersona(tipoPersona);
			persona.setUsuarioCreacion(usuarioCreacion);
			
			persona.setCuenta(cuenta);
				return persona; 
			 }
	 
	 public Cuenta getCuenta() {
		 Cuenta cuenta = new Cuenta();
		 cuenta.setAceptaPoliticaPrivacidad(aceptaPoliticaPrivacidad);
		 cuenta.setAceptaRecibirInformacion(aceptaRecibirInformacion);
		 cuenta.setAceptaTerminoUso(aceptaTerminoUso);
		 cuenta.setClave(clave);
		 cuenta.setEmail(email);
		 if(this.tipoPersona.contains("NAT")) {
			 cuenta.setPersona(getPersonaNat(cuenta));
		 }
		 else if(this.tipoPersona.contains("JUR")) {
			 cuenta.setPersona(getPersonaJur(cuenta));
		 }
		 cuenta.setIdCuenta(identificacion);
		 cuenta.setTipoContacto(tipoContacto);
		 cuenta.setUsuario(identificacion);
		 cuenta.setUsuarioContacto(usuarioContacto);
		 cuenta.setUsuarioCreacion(usuarioCreacion);
		 return cuenta;
	 }
	 
	
}
