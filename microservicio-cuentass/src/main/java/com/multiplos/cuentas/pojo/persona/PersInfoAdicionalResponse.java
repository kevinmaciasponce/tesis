package com.multiplos.cuentas.pojo.persona;

import com.multiplos.cuentas.models.BancariaResponse;
import com.multiplos.cuentas.models.PersonaEstadoFinanciero;
import com.multiplos.cuentas.models.PersonaFirma;
import com.multiplos.cuentas.models.PersonaTipoCuenta;
import com.multiplos.cuentas.pojo.formulario.FormDomicilioResponse;
import com.multiplos.cuentas.pojo.formulario.FormRepresentanteLegalResponse;

import lombok.Data;

@Data
public class PersInfoAdicionalResponse {

	private String estadoCivil;
	private String sexo;
	private String numeroTelefono;
	private String fuenteIngresos;
	private String cargoPersona;
	private String residenteDomicilioFiscal;
	private Long paisDomicilioFiscal;
	private FormDomicilioResponse personaDomicilio;
	private BancariaResponse bancaria;
	private PersonaEstadoFinanciero personaEstadoFinanciero;
	private FormRepresentanteLegalResponse personaRepresentanteLegal;
	private PersonaFirma personaFirmaAutorizada;
	private DocIdentificacionResponse personaDocumento;
}
