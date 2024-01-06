package com.multiplos.cuentas.pojo.formulario;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FormNatRequest {

//	@NotNull
//	private Long numeroSolicitud;
//	@NotNull
//	private String tipoPersona;
//	@NotNull
//	private String tipoCliente;
//	@NotNull(message = "usuario {NotNull}")
//	private String usuario;
//	@Size(min = 2, max = 50, message = "nombres {Size.nombreApellido}")
//	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "nombres {Pattern.letras}")
//	private String nombres;
//	@Size(min = 2, max = 50, message = "apellidos {Size.nombreApellido}")
//	@Pattern(regexp="[A-Za-ÁÉÍÓÚáéíóú ]+",message = "apellidos {Pattern.letras}")
//	private String apellidos;
	@NotNull(message = "identificación {NotNull}")
//	@Size(min = 10, max = 13, message = "{Size.identificacion}")
	@Pattern(regexp="[0-9]+",message = "identificación {Pattern.numeros}")
	private String identificacion;
	@NotNull(message = "celular {NotNull}")
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.CuentaRequest.numeroCelular}")
	private String numeroCelular;
	@NotNull(message = "estado civil {NotNull}")
	private String estadoCivil;
	@NotNull(message = "sexo {NotNull}")
	private String sexo;
	@NotNull(message = "nacionalidad {NotNull}")
	private Long nacionalidad;
	@Pattern(regexp="[0-9]{10}",message = "{Pattern.numeroTelefono}")
	private String telefonoAdicional;
	@NotNull(message = "Fuente de ingresos {NotNull}")
	private String fuenteIngresos;
	@NotNull(message = "Cargo {NotNull}")
	@Pattern(regexp="[A-Za-z_]+",message = "Cargo {Pattern.letras}")
	private String cargo;
	@NotNull
	private String residenteDomicilioFiscal;
	private Long paisDomicilioFiscal;
	//@NotNull
	private String aceptaLicitudFondos;
	//@NotNull
	private String aceptaInformacionCorrecta;
	@NotNull(message = "Datos de cuenta {NotNull}")
	private FormTipoCuentaRequest tipoCuenta;
	@NotNull(message = "Datos de Domicilio {NotNull}")
	private FormDomicilioRequest domicilio;		
}
