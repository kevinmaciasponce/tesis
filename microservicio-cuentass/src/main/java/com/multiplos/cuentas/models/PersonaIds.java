package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersonaIds implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "id_persona")
	private Long idPersona;
	
	private String identificacion;

	public PersonaIds() {
	}

	public PersonaIds(Long idPersona, String identificacion) {
		this.idPersona = idPersona;
		this.identificacion = identificacion;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idPersona, identificacion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonaIds other = (PersonaIds) obj;
		return Objects.equals(idPersona, other.idPersona) && Objects.equals(identificacion, other.identificacion);
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	

}
