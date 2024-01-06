package com.multiplos.cuentas.auspicios.models;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BeneficiarioResponse {
	
	//private BeneficiarioPersonaResponse persona;
	
	private String banco;
	private String tipoCuenta;
	private String numeroCuenta;
	private String identificacion;
	private String representante;
	private String Disciplina;
	private String Categoria;
	private String Modalidad;
	private String correo;
	private String perfil;
	private String tituloActual;
	private String ruta2;
	private String ruta1;
}
