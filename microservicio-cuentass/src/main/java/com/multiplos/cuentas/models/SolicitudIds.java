package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SolicitudIds implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_solicitud")
	private Long idSolicitud;
	
	@Column(name = "numero_solicitud")
	private Long numeroSolicitud;

	public SolicitudIds() {
	}

	public SolicitudIds(Long idSolicitud, Long numeroSolicitud) {
		this.idSolicitud = idSolicitud;
		this.numeroSolicitud = numeroSolicitud;
	}

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public Long getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(Long numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}

	@Override
	public String toString() {
		return "SolicitudIds [idSolicitud=" + idSolicitud + ", numeroSolicitud=" + numeroSolicitud + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(idSolicitud, numeroSolicitud);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolicitudIds other = (SolicitudIds) obj;
		return Objects.equals(idSolicitud, other.idSolicitud) && Objects.equals(numeroSolicitud, other.numeroSolicitud);
	}
	
	
	
}
