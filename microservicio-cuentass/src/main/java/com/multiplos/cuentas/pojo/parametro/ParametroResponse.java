package com.multiplos.cuentas.pojo.parametro;

public class ParametroResponse {
	
	private Long idParametro;
	private String codParametro;
	private String valor;
	private String descripcion;
	
	public ParametroResponse() {
	}
	public Long getIdParametro() {
		return idParametro;
	}
	public void setIdParametro(Long idParametro) {
		this.idParametro = idParametro;
	}
	public String getCodParametro() {
		return codParametro;
	}
	public void setCodParametro(String codParametro) {
		this.codParametro = codParametro;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

}
