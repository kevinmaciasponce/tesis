package com.multiplos.cuentas.pojo.formulario;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class FormJurRequest {

	@NotNull
	private Long numeroSolicitud;
	@NotNull
	private String tipoPersona;
	@NotNull
	private String tipoCliente;
	@NotNull(message = "usuario {NotNull}")
	private String usuario;
	@NotNull(message = "Razón Social {NotNull}")
	private String razonSocial;
	@NotNull(message = "RUC {NotNull}")
	@Pattern(regexp="[0-9]{13}",message = "{Pattern.ruc}")
	private String ruc;
	@NotNull(message = "Actividad económica {NotNull}")
	private String actividadEconomica;
	@NotNull(message = "Correo {NotNull}")
	@Email(message = "{Email}")
	private String email;
	@NotNull(message = "Nombre Contacto {NotNull}")
	private String nombreContacto;
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.numeroTelefono}")
	private String numeroTelefono;
	@NotNull(message = "Número de celular {NotNull}")
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.CuentaRequest.numeroCelular}")
	private String numeroCelular;
	@NotNull(message = "Fuente de ingresos {NotNull}")
	private String fuenteIngresos;
	@NotNull
	private String residenteDomicilioFiscal;
	private Long paisDomicilioFiscal;
	@NotNull(message = "Acepta licitud de fondos {NotNull}")
	private String aceptaLicitudFondos;
	@NotNull(message = "Acepta informacion correcta {NotNull}")
	private String aceptaInformacionCorrecta;
	@NotNull(message = "Datos de cuenta {NotNull}")
	private FormTipoCuentaRequest tipoCuenta;
	@NotNull(message = "Datos de Domicilio {NotNull}")
	private FormDomicilioRequest domicilio;
	@NotNull(message = "Estado financiero {NotNull}")
	private FormJurEstFinanRequest estadoFinanciero;
	@NotNull(message = "Representante legal {NotNull}")
	private FormJurRepreLegalRequest representanteLegal;
	@NotNull(message = "Firma Autorizada {NotNull}")
	private FormFirmaAutorizadaRequest firmaAutorizada;

}
