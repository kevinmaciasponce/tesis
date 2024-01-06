package com.multiplos.cuentas.models;

import org.hibernate.annotations.ColumnTransformer;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BadResponse {

	
	private static final long serialVersionUID = 1L;
	
	private String error;
	private String code;
	private String interno="S/N";
	@JsonRawValue
	private String errorRest="S/N";
	
	public BadResponse(String error) {
		this.error = error;
	}
	public BadResponse(String error,String interno) {
		this.error = error;
		this.interno= interno;
	}
	public BadResponse(String error, String code, String interno) {
		super();
		this.error = error;
		this.code = code;
		this.interno = interno;
	}
	public BadResponse(String error, String code, String interno, String resultRawJson) {
		super();
		this.error = error;
		this.code = code;
		this.interno = interno;
		this.errorRest = resultRawJson;
	}
}
